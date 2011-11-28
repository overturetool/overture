package org.overture.pog.tests;

enum FunctionalTests implements AbstractTests
{
	ADTSL("functional_tests/ADTSL"), AlarmSL("functional_tests/AlarmSL"), ATCSL(
			"functional_tests/ATCSL"), barSL("functional_tests/barSL"), BOMSL(
			"functional_tests/BOMSL"), cashdispenserSL(
			"functional_tests/cashdispenserSL"), CMSL("functional_tests/CMSL"), CountryColouringSL(
			"functional_tests/CountryColouringSL"), crosswordSL(
			"functional_tests/crosswordSL"), DFDexampleSL(
			"functional_tests/DFDexampleSL"), DigraphSL(
			"functional_tests/DigraphSL"), dwarfSL("functional_tests/dwarfSL"), EngineSL(
			"functional_tests/EngineSL"), expressSL(
			"functional_tests/expressSL"), gatewaySL(
			"functional_tests/gatewaySL"), graphedSL(
			"functional_tests/graphedSL"), hotelSL("functional_tests/hotelSL"), librarySL(
			"functional_tests/librarySL"), looseSL("functional_tests/looseSL"), LUPSLSL(
			"functional_tests/LUPSLSL"), MAASL("functional_tests/MAASL"), metroSL(
			"functional_tests/metroSL"), monitorSL("functional_tests/monitorSL"), NDBSL(
			"functional_tests/NDBSL"), newspeakSL("functional_tests/newspeakSL"), pacemakerSL(
			"functional_tests/pacemakerSL"), PlannerSL(
			"functional_tests/PlannerSL"), ProgLangSL(
			"functional_tests/ProgLangSL"), raildirSL(
			"functional_tests/raildirSL"), realmSL("functional_tests/realmSL"), recursiveSL(
			"functional_tests/recursiveSL"), SAFERSL("functional_tests/SAFERSL"), shmemSL(
			"functional_tests/shmemSL"), simulatorSL(
			"functional_tests/simulatorSL"), soccerSL(
			"functional_tests/soccerSL"), STVSL("functional_tests/STVSL"), telephoneSL(
			"functional_tests/telephoneSL"), TrackerSL(
			"functional_tests/TrackerSL"), trafficSL(
			"functional_tests/trafficSL"), ;
	private String filePath;

	FunctionalTests(String fp)
	{
		this.filePath = fp;
	}

	public String getFolder()
	{
		return this.filePath;
	}
};
