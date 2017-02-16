package application

/*
Querying Patstat using SQL only can be a complex and error-prone.
Queries become much easier to build and understand if we refactor the relational model to an object oriented and functional model:

case class Inventor(id: Int, name: String, country: String, applications: Set[Int])
case class Application(id: Int, id2: String, registerAuthority: String, inventorIds: Set[Int], citations: Set[Int])

$ val (applications: Set[Application], inventors: Set[Inventor], applicationMap: Map[Int, Application], inventorMap: Map[Int, Inventor]) = buildDataset()

$ showNumApplicationsPerApplicant
#applications  person_id      person_name
451            7982008        Ecole Polytechnique Federale de Lausanne (EPFL)
205            5865291        Ecole Polytechnique Federale de Lausanne
158            6112321        Ecole Polytechnique Federale de Lausanne (EPFL)
105            9032682        Ecole Polytechnique Federale de Lausanne
103            8548693        Ecole Polytechnique Federale de Lausanne (EPFL)
92             5544832        Ecole Polytechnique Federale de Lausanne
38             193            ECOLE POLYTECHNIQUE FEDERALE DE LAUSANNE
29             24540381       ECOLE POLYTECHNIQUE FEDERALE DE LAUSANNE (EPFL), LAUSANNE
25             24541662       ECOLE POLYTECHNIQUE FEDERALE DE LAUSANNE, LAUSANNE
...

totalNumApplications: 1777

Set(
  Application(id = 200951, id2 = "EP08163816A", registerAuthority = "EP", inventorIds = Set(277197, 298560, 298561, 298562), citations = Set()),
  Application(id = 55139430, id2 = "EP08709042A", registerAuthority = "EP", inventorIds = Set(1192249, 4108306, 4108307), citations = Set(341424867, 364649977, 335517053, 417440248)),
  Application(id = 19107072, id2 = "FR0603494A", registerAuthority = "FR", inventorIds = Set(26794051, 5331708, 26794048, 26794050, 26794049, 5865291, 26794052, 26783558, 12607589), citations = Set(352824643)),
  ...

Set(
  Inventor(id = 1337448, name = "VAN DEN BERGH, Hubert", country = "CH", applications = Set(15975895)),
  Inventor(id = 1213249, name = "Gabaglio, Vincent", country = "CH", applications = Set(15905906)),
  Inventor(id = 47837090, name = "ZUPPIROLI, Libero", country = "CH", applications = Set(17261007)),
  ...

# applications: 1770
# inventors: 3877


// inventors per country
$ inventors.groupBy(_.country).mapValues(_.size).toList.sortBy(- _._2).foreach(println)
(CH,2704)
(,475)
(FR,145)
(DE,125)
(US,97)
...

// histogram of num of applications per invetor
$ showHistogram(inventors.toList.map(_.applications.size))
(1,2930)
(2,471)
(3,183)
(4,103)
(5,60)
...

// histogram of num of citations per application
$ applications.toList.map(_.citations.size)
(0,1171)
(1,147)
(2,82)
(3,54)
(4,47)
(5,22)
...

// numOfCountriesByApplication
$ applications.map(a => (a.id, a.inventorIds.map(inventorMap).map(_.country).size))
(52404807,1)
(17387736,1)
(415907527,1)
(334014686,1)
(220218,1)
...

// numOfCountriesApplicationCount
$ numOfCountriesByApplication.groupBy(_._2).mapValues(_.map(_._1).size).toList.sortBy(_._1)
(1,1232)
(2,427)
(3,93)
(4,14)
(5,4)


// numApplicationsFractionalCountByCountries
def inventorCountriesFractionalCount(country: String)(application: Application): Double = {
  val countryListOfThisApplication = application.inventorIds.map(inventorMap).toList.map(_.country)
  countryListOfThisApplication.count(_ == country).toDouble / countryListOfThisApplication.size
}

def numApplicationsFractionalCount(country: String): Double =
  applications.map(inventorCountriesFractionalCount(country)).sum

val countries: Set[String] = inventors.map(_.country)

buildMap(countries, numApplicationsFractionalCount).toList.sortBy(-_._2)

(CH,14.512626262626265)
(,11.112085137085138)
(DE,5.854112554112554)
(FR,5.056493506493506)
(US,4.406349206349206)
...
*/


import java.sql.Connection

import anorm._

import scala.collection.immutable.Seq

object EPFLPatentsProject {
  case class Inventor(id: Int, name: String, country: String, applications: Set[Int])
  case class Application(id: Int, id2: String, registerAuthority: String, familyId: Int, inventorIds: Set[Int], citations: Set[Int])
  case class ApplicationFamily(id: Int, patentIds: Set[Int], priorityPatentIds: Set[Int])


  def main(args: Array[String]) {
    conn = getDbConnection(dbUrl = args(0))

    val (applications: Set[Application], applicationFamilies: Set[ApplicationFamily], inventors: Set[Inventor], applicationMap: Map[Int, Application], inventorMap: Map[Int, Inventor]) = buildDataset()

    pprint.pprintln(applications, width = 10000)
    pprint.pprintln(applicationFamilies, width = 10000)
    pprint.pprintln(inventors, width = 10000)

    println("# applications: " + applications.size)
    println("# applicationFamilies: " + applicationFamilies.size)
    println("# inventors: " + inventors.size)

    println("inventors per country")
    inventors.groupBy(_.country).mapValues(_.size).toList.sortBy(- _._2).foreach(println)

    println("histogram of num of applications per inventor")
    showHistogram(inventors.toList.map(_.applications.size))

    println("histogram of num of citations per application")
    showHistogram(applications.toList.map(_.citations.size))

    val numOfCountriesByApplication: Set[(Int, Int)] =
      applications.map(a => (a.id, a.inventorIds.map(inventorMap).map(_.country).size))
    printList("+++ numOfCountriesByApplication", numOfCountriesByApplication)

    val numOfCountriesApplicationCount: Seq[(Int, Int)] =
      numOfCountriesByApplication.groupBy(_._2).mapValues(_.map(_._1).size).toList.sortBy(_._1)
    printList("+++ numOfCountriesApplicationCount", numOfCountriesApplicationCount)


    val numApplicationsFractionalCountByCountries = {
      def inventorCountriesFractionalCount(country: String)(application: Application): Double = {
        val countryListOfThisApplication = application.inventorIds.map(inventorMap).toList.map(_.country)
        countryListOfThisApplication.count(_ == country).toDouble / countryListOfThisApplication.size
      }

      def numApplicationsFractionalCount(country: String): Double =
        applications.map(inventorCountriesFractionalCount(country)).sum

      val countries: Set[String] = inventors.map(_.country)

      buildMap(countries, numApplicationsFractionalCount).toList.sortBy(-_._2)
    }
    printList("+++ numApplicationsFractionalCountByCountries", numApplicationsFractionalCountByCountries)



  }

  def showHistogram(values: Seq[Int]) {
    values.groupBy(identity).mapValues(_.size).toList.sortBy(_._1).foreach(println)
  }

  def getDbConnection(dbUrl: String): Connection = {
    Class.forName("com.mysql.jdbc.Driver").newInstance()
    java.sql.DriverManager.getConnection(dbUrl)
  }

  implicit var conn: Connection = _

  def buildDataset(): (Set[Application], Set[ApplicationFamily], Set[Inventor], Map[Int, Application], Map[Int, Inventor]) = {
    val applicantIds: Set[Int] =
      Set(193, 35957, 77955, 89041, 113677, 113682, 138443, 138448, 153705, 159755, 194754, 235285, 255476, 266601, 277197, 305799, 340119, 354081, 366072, 385932, 609867, 632009, 800002, 866883, 867483, 867774, 868169, 905158, 905423, 905427, 907858, 1087274, 1142664, 1152729, 1184096, 1192249, 1392763, 1575636, 1646785, 1757006, 1778604, 1962549, 2042735, 2171193, 2178133, 2846536, 3077935, 3625022, 3879918, 4097351, 4118049, 4133525, 4163558, 4172651, 4195033, 4275253, 4290200, 4325883, 4325886, 4356979, 4369791, 4430816, 4525299, 4534175, 4555053, 4620030, 4698880, 4714230, 4747154, 4792837, 4794858, 4839150, 4885488, 4924430, 4929213, 5022780, 5129147, 5202837, 5544832, 5671347, 5714339, 5865291, 6109963, 6112321, 6115831, 6473909, 6542911, 6566563, 6887377, 7380875, 7410876, 7712619, 7862861, 7923525, 7943008, 7982008, 8184914, 8197853, 8365485, 8413838, 8548693, 8641515, 8727024, 8747364, 8828198, 8828200, 8939992, 8961882, 9032682, 9396906, 9396908, 9611372, 9685486, 9915714, 10469096, 10514806, 10577882, 10771032, 10804000, 11029610, 11094109, 11104754, 11419862, 11478195, 11518840, 11599458, 11627052, 11657086, 11686706, 11758246, 11815523, 11831273, 11859920, 11923783, 11947131, 12010118, 12205142, 12261169, 12413066, 12524005, 12995595, 14049589, 14604600, 14879243, 14965767, 15095069, 15748286, 15784002, 16723058, 16754805, 16834558, 16850846, 16861331, 17438869, 17469273, 17568314, 17576327, 17665573, 17752361, 17932744, 17932859, 17937111, 17937691, 17938602, 19358484, 19738394, 20071795, 20071799, 23186498, 24130767, 24261512, 24540381, 24541662, 25645530, 25661566, 25682902, 25683882, 25684325, 25689336, 25689486, 25695852, 27889864, 27918116, 28084113, 28967576, 28974534, 28978073, 29024729, 29576019, 29713518, 32259840, 32267576, 32583863, 33317066, 34001112, 40090082, 40200842, 40291530, 40739919, 40806476, 41577517, 41680217, 42341462, 42757889, 43382378, 43461806, 43694420, 44171822, 44896367, 44924546, 45163176, 45164387, 45178426, 45189063, 45196579, 45197201, 45199396, 45201514, 45228562, 45337055, 45341178, 45386018, 45387073, 45392926, 46604002, 46700734, 46945923, 46950030, 46955143, 47148613, 47156986, 47160693, 47222150, 47222966, 47251154, 47415932, 47548044, 47683035, 47836074, 47841344, 48279665, 48279666, 48777851, 48959392)
//      getEPFLApplicants()   // it takes a while to compute...

    showNumApplicationsPerPerson(applicantIds)

    val applications: Set[ApplicationC1] =
      getApplications(applicantIds)

    val applicationIds: Set[Int] =
      applications.map(_.appln_id)

    val priorityApplicationsIds: Map[Int, Set[Int]] =
      getPriorityApplicationIds(applicationIds)

    val inventorsByApplication: Map[Int, Set[Int]] =
      getInventorsByApplication(applicationIds)

    val inventorIds: Set[Int] =
      inventorsByApplication.values.flatten.toSet

    val inventors: Set[InventorC1] =
      getPersons(inventorIds)

    val citationsByApplication: Map[Int, Set[Int]] =
      getCitationsByApplication(applicationIds)

    val refactoredApplications : Set[Application] =
      refactorApplications(applications, inventorsByApplication, citationsByApplication)

    val refactoredInventors: Set[Inventor] =
      refactorInventors(inventors, refactoredApplications)

    val applicationFamilies: Set[ApplicationFamily] =
      buildApplicationFamilies(refactoredApplications, priorityApplicationsIds)

    val applicationMap: Map[Int, Application] =
      refactoredApplications.map(a => (a.id, a)).toMap

    val inventorMap: Map[Int, Inventor] =
      refactoredInventors.map(i => (i.id, i)).toMap

    (refactoredApplications, applicationFamilies, refactoredInventors, applicationMap, inventorMap)
  }

  def getEPFLApplicants()(implicit conn: Connection): Set[Int] =
    SQL"""
          select person_id from TLS906_PERSON where
            (person_name regexp '[[:<:]]epfl[[:>:]]' or person_name regexp 'cole.*.laus') and
            person_id not in (22431516, 17564605, 24538940, 3064586, 44889619, 3683520, 48854061)
    """.as(SqlParser.int(1).*).toSet

  case class PersonC1(person_id: Int, person_name: String, num_applications: Int)
  def showNumApplicationsPerPerson(personIds: Set[Int])(implicit conn: Connection) {
    println("+++ showNumApplicationsPerApplicant")
    val q = SQL"""
        select
          t3.person_id,
          t3.person_name,
          count(*) as num_applications
        from
          TLS201_APPLN t1
          left join TLS207_PERS_APPLN t2 on t1.appln_id = t2.appln_id
          left join TLS906_PERSON t3 on t2.person_id = t3.person_id
        where
          t2.applt_seq_nr > 0 and
          t3.person_id in ($personIds)
        group by t2.person_id
        order by num_applications desc
      """.as(Macro.namedParser[PersonC1].*)

    println("#applications\tperson_id\tperson_name")
    q.foreach(r => println(s"${r.num_applications}\t${r.person_id}\t${r.person_name}"))

    val totalNumApplications = q.map(_.num_applications).sum
    println(s"totalNumApplications: $totalNumApplications")
  }

  case class ApplicationC1(appln_id: Int, appln_auth: String, appln_nr: String, appln_kind: String, inpadoc_family_id: Int)
  def getApplications(applicantIds: Set[Int])(implicit conn: Connection): Set[ApplicationC1] =
    SQL"""
       select distinct
         t1.appln_id,
         t1.appln_auth,
         t1.appln_nr,
         t1.appln_kind,
         t3.inpadoc_family_id
       from
         TLS201_APPLN t1
         left join TLS207_PERS_APPLN t2 on t1.appln_id = t2.appln_id
         left join TLS219_INPADOC_FAM t3 on t1.appln_id = t3.appln_id
       where t2.person_id in ($applicantIds)
      """.as(Macro.namedParser[ApplicationC1].*).toSet

  def getPriorityApplicationIds(applicationIds: Set[Int])(implicit conn: Connection): Map[Int, Set[Int]] =
    SQL"""
       select appln_id, prior_appln_id from TLS204_APPLN_PRIOR where appln_id in ($applicationIds)
       """.as((SqlParser.int(1) ~ SqlParser.int(2)).*).groupBy(_._1).mapValues(_.map(_._2).toSet)

  def getInventorsByApplication(applicationIds: Set[Int])(implicit conn: Connection): Map[Int, Set[Int]] = {
    val parser = SqlParser.int(1) ~ SqlParser.int(2)
    val list =
      SQL"""
         select t1.appln_id, t2.person_id
         from TLS201_APPLN t1 left join TLS207_PERS_APPLN t2 ON t1.appln_id = t2.appln_id
         where t1.appln_id in ($applicationIds)
      """.as(parser.*)

    list.groupBy(_._1).mapValues(_.map(_._2).toSet)
  }

  def getCitationsByApplication(applicationIds: Set[Int])(implicit conn: Connection): Map[Int, Set[Int]] = {
    val parser = SqlParser.int(1) ~ SqlParser.int(2)
    val list =
      SQL"""
         select t1.appln_id, t3.appln_id
         from
           TLS211_PAT_PUBLN t1
           inner join TLS212_CITATION t2 ON t1.pat_publn_id = t2.cited_pat_publn_id
           left join TLS211_PAT_PUBLN t3 ON t2.pat_publn_id = t3.pat_publn_id
         where t1.appln_id in ($applicationIds)
      """.as(parser.*)

    list.groupBy(_._1).mapValues(_.map(_._2).toSet)
  }

  case class InventorC1(id: Int, name: String, country: String)
  def getPersons(personIds: Set[Int])(implicit conn: Connection): Set[InventorC1] =
    SQL"""
       select person_id as id, person_name as name, person_ctry_code as country from TLS906_PERSON where person_id in ($personIds)
    """.as(Macro.namedParser[InventorC1].*).toSet

  def id2(a: ApplicationC1): String = a.appln_auth + a.appln_nr + a.appln_kind

  def refactorApplications(
                            applications: Set[ApplicationC1],
                            inventorsByApplication: Map[Int, Set[Int]],
                            citationsByApplication: Map[Int, Set[Int]]): Set[Application] =
    applications.map(a =>
      Application(
        a.appln_id,
        id2(a),
        a.appln_auth,
        a.inpadoc_family_id,
        inventorsByApplication(a.appln_id),
        citationsByApplication.withDefaultValue(Set.empty)(a.appln_id)
      )
    )

  def inventorApplications(inventorId: Int, applications: Set[Application]): Set[Application] =
    applications.filter(a => a.inventorIds.contains(inventorId))

  def refactorInventors(inventors: Set[InventorC1], refactoredApplications : Set[Application]): Set[Inventor] =
    inventors.map(i =>
      Inventor(i.id, i.name, i.country, inventorApplications(i.id, refactoredApplications).map(_.id))
    )

  def buildApplicationFamilies(applications: Set[Application], priorityApplicationsIds: Map[Int, Set[Int]]): Set[ApplicationFamily] =
    applications.map(_.familyId).map { familyId =>
      val applicationsOfThisFamily: Set[Application] = applications.filter(_.familyId == familyId)
      val applicationIdsOfThisFamily = applicationsOfThisFamily.map(_.id)
      ApplicationFamily(
        familyId,
        patentIds = applicationIdsOfThisFamily,
        priorityPatentIds = applicationIdsOfThisFamily.flatMap(priorityApplicationsIds.withDefaultValue(Set.empty)))
    }

  def printList[T](title: String, list: Traversable[T]) {
    println(s"$title, count: ${list.size}")
    list.foreach(println)
    println()
  }

  def buildMap[K,V](keys: Traversable[K], fn: (K) => V)(implicit ord: Ordering[K]): Traversable[(K, V)] =
    keys.map(k => (k, fn(k)))
}
