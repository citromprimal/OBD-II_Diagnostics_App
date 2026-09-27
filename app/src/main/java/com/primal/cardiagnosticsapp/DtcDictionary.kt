package com.primal.cardiagnosticsapp

import com.primal.cardiagnosticsapp.data.DtcSeverity
import com.primal.cardiagnosticsapp.ui.theme.AppLanguage

object DtcDictionary {

    data class DtcMeta(
        val code: String,
        val description: String,
        val severity: DtcSeverity,
        val likelyCauses: List<String>,
        val recommendation: String?,
        val isGeneric: Boolean
    )

    private data class DtcEntry(
        val en: String,
        val sk: String,
        val severity: DtcSeverity? = null,
        val causesEn: List<String> = emptyList(),
        val causesSk: List<String> = emptyList(),
        val recommendationEn: String? = null,
        val recommendationSk: String? = null
    )

    private val entries = mapOf(
        "P0100" to DtcEntry(
            en = "MAF circuit malfunction",
            sk = "Porucha obvodu MAF snímača",
            severity = DtcSeverity.WARNING,
            causesEn = listOf("Faulty MAF sensor", "Damaged wiring", "Loose connector", "Intake air leak"),
            causesSk = listOf("Chybný MAF snímač", "Poškodená kabeláž", "Uvoľnený konektor", "Netesnosť sania"),
            recommendationEn = "Check the MAF connector, intake hose and sensor wiring.",
            recommendationSk = "Skontrolujte konektor MAF, hadicu sania a kabeláž snímača."
        ),
        "P0101" to DtcEntry(
            en = "MAF circuit range/performance",
            sk = "MAF rozsah/výkon",
            severity = DtcSeverity.WARNING,
            causesEn = listOf("Dirty MAF sensor", "Air leak after MAF", "Restricted intake", "Incorrect sensor reading"),
            causesSk = listOf("Znečistený MAF snímač", "Netesnosť za MAF", "Obmedzené sanie", "Nesprávny signál snímača"),
            recommendationEn = "Inspect and clean the MAF sensor and check the intake tract for leaks.",
            recommendationSk = "Skontrolujte a vyčistite MAF snímač a overte netesnosti v sacom trakte."
        ),
        "P0102" to DtcEntry(
            en = "MAF circuit low input",
            sk = "MAF nízky vstup",
            severity = DtcSeverity.WARNING,
            causesEn = listOf("Low MAF signal", "Wiring fault", "Sensor contamination"),
            causesSk = listOf("Nízky signál MAF", "Porucha kabeláže", "Znečistený snímač"),
            recommendationEn = "Check MAF signal voltage and sensor wiring.",
            recommendationSk = "Skontrolujte napätie signálu MAF a kabeláž snímača."
        ),
        "P0103" to DtcEntry(
            en = "MAF circuit high input",
            sk = "MAF vysoký vstup",
            severity = DtcSeverity.WARNING,
            causesEn = listOf("Short to power", "Sensor fault", "Connector issue"),
            causesSk = listOf("Skrat na plus", "Porucha snímača", "Problém konektora"),
            recommendationEn = "Inspect the MAF connector and signal circuit for shorts.",
            recommendationSk = "Skontrolujte konektor MAF a signálny obvod kvôli skratu."
        ),
        "P0113" to DtcEntry(
            en = "Intake air temperature sensor 1 circuit high",
            sk = "Vysoký signál snímača teploty nasávania",
            severity = DtcSeverity.WARNING,
            causesEn = listOf("Open circuit", "Sensor fault", "Connector disconnected"),
            causesSk = listOf("Prerušený obvod", "Porucha snímača", "Odpojený konektor"),
            recommendationEn = "Inspect the intake air temperature sensor and connector.",
            recommendationSk = "Skontrolujte snímač teploty nasávania a jeho konektor."
        ),
        "P0115" to DtcEntry(
            en = "Engine coolant temperature circuit malfunction",
            sk = "Porucha obvodu teploty chladiacej kvapaliny",
            severity = DtcSeverity.WARNING,
            causesEn = listOf("ECT sensor fault", "Wiring issue", "Connector corrosion"),
            causesSk = listOf("Porucha ECT snímača", "Problém kabeláže", "Korózia konektora"),
            recommendationEn = "Check coolant temperature sensor data and wiring continuity.",
            recommendationSk = "Skontrolujte dáta snímača teploty chladiacej kvapaliny a kontinuitu kabeláže."
        ),
        "P0117" to DtcEntry(
            en = "Engine coolant temperature circuit low input",
            sk = "Nízky signál teploty chladiacej kvapaliny",
            severity = DtcSeverity.WARNING,
            causesEn = listOf("Short to ground", "Sensor fault"),
            causesSk = listOf("Skrat na zem", "Porucha snímača"),
            recommendationEn = "Inspect sensor voltage and possible short-to-ground.",
            recommendationSk = "Skontrolujte napätie snímača a možný skrat na zem."
        ),
        "P0118" to DtcEntry(
            en = "Engine coolant temperature circuit high input",
            sk = "Vysoký signál teploty chladiacej kvapaliny",
            severity = DtcSeverity.WARNING,
            causesEn = listOf("Open circuit", "Sensor unplugged", "Sensor fault"),
            causesSk = listOf("Prerušený obvod", "Odpojený snímač", "Porucha snímača"),
            recommendationEn = "Inspect the coolant temperature sensor connector and harness.",
            recommendationSk = "Skontrolujte konektor a zväzok snímača teploty chladiacej kvapaliny."
        ),
        "P0128" to DtcEntry(
            en = "Coolant thermostat (coolant temperature below thermostat regulating temperature)",
            sk = "Termostat chladiacej kvapaliny (teplota pod regulovanou)",
            severity = DtcSeverity.INFO,
            causesEn = listOf("Thermostat stuck open", "Coolant temp sensor inaccurate", "Low coolant"),
            causesSk = listOf("Termostat zaseknutý otvorený", "Neprpresný snímač teploty", "Nízka hladina chladiacej kvapaliny"),
            recommendationEn = "Check thermostat operation and coolant warm-up behavior.",
            recommendationSk = "Skontrolujte funkciu termostatu a priebeh zohrievania motora."
        ),
        "P0120" to DtcEntry(
            en = "Throttle/Pedal position sensor A circuit",
            sk = "Obvod snímača polohy škrtiacej klapky/pedála A",
            severity = DtcSeverity.WARNING,
            causesEn = listOf("Throttle position sensor fault", "Pedal sensor issue", "Wiring problem"),
            causesSk = listOf("Porucha snímača polohy klapky", "Problém snímača pedála", "Problém kabeláže"),
            recommendationEn = "Check throttle/pedal position sensor signals and connector state.",
            recommendationSk = "Skontrolujte signály snímača klapky/pedála a stav konektora."
        ),
        "P0121" to DtcEntry(
            en = "Throttle/Pedal position sensor A range/performance",
            sk = "Rozsah/výkon snímača polohy škrtiacej klapky/pedála A",
            severity = DtcSeverity.WARNING,
            causesEn = listOf("Sensor out of range", "Sticky throttle body", "Signal inconsistency"),
            causesSk = listOf("Snímač mimo rozsahu", "Zasekávajúca sa klapka", "Nekonzistentný signál"),
            recommendationEn = "Check throttle body movement and sensor calibration.",
            recommendationSk = "Skontrolujte pohyb škrtiacej klapky a kalibráciu snímača."
        ),
        "P0122" to DtcEntry(
            en = "Throttle/Pedal position sensor A circuit low",
            sk = "Nízky signál snímača polohy škrtiacej klapky/pedála A",
            severity = DtcSeverity.WARNING,
            causesEn = listOf("Short to ground", "Sensor failure", "Damaged wiring"),
            causesSk = listOf("Skrat na zem", "Porucha snímača", "Poškodená kabeláž"),
            recommendationEn = "Inspect the low-voltage signal path and connector.",
            recommendationSk = "Skontrolujte nízkonapäťovú signálnu cestu a konektor."
        ),
        "P0123" to DtcEntry(
            en = "Throttle/Pedal position sensor A circuit high",
            sk = "Vysoký signál snímača polohy škrtiacej klapky/pedála A",
            severity = DtcSeverity.WARNING,
            causesEn = listOf("Short to voltage", "Sensor fault", "Connector issue"),
            causesSk = listOf("Skrat na napájanie", "Porucha snímača", "Problém konektora"),
            recommendationEn = "Check the signal wire for short-to-power and verify sensor output.",
            recommendationSk = "Skontrolujte signálny vodič kvôli skratu na napájanie a overte výstup snímača."
        ),
        "P0130" to DtcEntry(
            en = "O2 sensor circuit (Bank 1 Sensor 1)",
            sk = "Obvod lambda sondy (Banka 1 Senzor 1)",
            severity = DtcSeverity.WARNING,
            causesEn = listOf("O2 sensor fault", "Wiring issue", "Exhaust leak"),
            causesSk = listOf("Porucha lambda sondy", "Problém kabeláže", "Netesnosť výfuku"),
            recommendationEn = "Check the front oxygen sensor wiring and exhaust leaks before the sensor.",
            recommendationSk = "Skontrolujte kabeláž prednej lambda sondy a netesnosti výfuku pred snímačom."
        ),
        "P0131" to DtcEntry(
            en = "O2 sensor low voltage (Bank 1 Sensor 1)",
            sk = "Nízke napätie lambda sondy (Banka 1 Senzor 1)",
            severity = DtcSeverity.WARNING,
            causesEn = listOf("Lean mixture", "Sensor fault", "Wiring issue"),
            causesSk = listOf("Chudobná zmes", "Porucha snímača", "Problém kabeláže"),
            recommendationEn = "Inspect O2 sensor signal and fuel trim values.",
            recommendationSk = "Skontrolujte signál lambda sondy a hodnoty korekcie zmesi."
        ),
        "P0132" to DtcEntry(
            en = "O2 sensor high voltage (Bank 1 Sensor 1)",
            sk = "Vysoké napätie lambda sondy (Banka 1 Senzor 1)",
            severity = DtcSeverity.WARNING,
            causesEn = listOf("Rich mixture", "Sensor fault", "Signal short"),
            causesSk = listOf("Bohatá zmes", "Porucha snímača", "Skrat signálu"),
            recommendationEn = "Inspect fuel trim, injector behavior and O2 sensor signal circuit.",
            recommendationSk = "Skontrolujte korekciu zmesi, správanie vstrekovačov a signálny obvod lambda sondy."
        ),
        "P0133" to DtcEntry(
            en = "O2 sensor circuit slow response (Bank 1 Sensor 1)",
            sk = "Pomalá odozva lambda sondy (Banka 1 Senzor 1)",
            severity = DtcSeverity.WARNING,
            causesEn = listOf("Aging O2 sensor", "Contamination", "Exhaust leak", "Fuel mixture issue"),
            causesSk = listOf("Starnúca lambda sonda", "Znečistenie", "Netesnosť výfuku", "Problém zmesi"),
            recommendationEn = "Check sensor switching speed and inspect for intake or exhaust leaks.",
            recommendationSk = "Skontrolujte rýchlosť prepínania sondy a overte netesnosti sania alebo výfuku."
        ),
        "P0134" to DtcEntry(
            en = "O2 sensor no activity detected (Bank 1 Sensor 1)",
            sk = "Bez aktivity lambda sondy (Banka 1 Senzor 1)",
            severity = DtcSeverity.WARNING,
            causesEn = listOf("Dead sensor", "Open circuit", "Disconnected connector"),
            causesSk = listOf("Mŕtvy snímač", "Prerušený obvod", "Odpojený konektor"),
            recommendationEn = "Inspect the sensor connector, heater circuit and signal line.",
            recommendationSk = "Skontrolujte konektor snímača, obvod ohrevu a signálny vodič."
        ),

        "P0170" to DtcEntry(
            en = "Fuel trim malfunction (Bank 1)",
            sk = "Porucha korekcie zmesi (Banka 1)",
            severity = DtcSeverity.WARNING,
            causesEn = listOf("Vacuum leak", "Fuel pressure issue", "MAF error", "O2 sensor fault"),
            causesSk = listOf("Netesnosť podtlaku", "Problém tlaku paliva", "Chyba MAF", "Porucha lambda sondy"),
            recommendationEn = "Inspect intake leaks, MAF readings and fuel pressure.",
            recommendationSk = "Skontrolujte netesnosti sania, hodnoty MAF a tlak paliva."
        ),
        "P0171" to DtcEntry(
            en = "System too lean (Bank 1)",
            sk = "Príliš chudobná zmes (Banka 1)",
            severity = DtcSeverity.WARNING,
            causesEn = listOf("Vacuum leak", "Dirty MAF", "Low fuel pressure", "Exhaust leak"),
            causesSk = listOf("Netesnosť podtlaku", "Znečistený MAF", "Nízky tlak paliva", "Netesnosť výfuku"),
            recommendationEn = "Inspect for intake leaks and compare MAF and fuel trim values.",
            recommendationSk = "Skontrolujte netesnosti sania a porovnajte MAF a korekcie zmesi."
        ),
        "P0172" to DtcEntry(
            en = "System too rich (Bank 1)",
            sk = "Príliš bohatá zmes (Banka 1)",
            severity = DtcSeverity.WARNING,
            causesEn = listOf("Leaking injector", "High fuel pressure", "Faulty MAF", "Faulty O2 sensor"),
            causesSk = listOf("Prepúšťajúci vstrekovač", "Vysoký tlak paliva", "Chybný MAF", "Chybná lambda sonda"),
            recommendationEn = "Inspect injector operation, fuel pressure and intake sensor values.",
            recommendationSk = "Skontrolujte činnosť vstrekovačov, tlak paliva a hodnoty snímačov sania."
        ),
        "P0174" to DtcEntry(
            en = "System too lean (Bank 2)",
            sk = "Príliš chudobná zmes (Banka 2)",
            severity = DtcSeverity.WARNING,
            causesEn = listOf("Vacuum leak", "Fuel supply issue", "Sensor error"),
            causesSk = listOf("Netesnosť podtlaku", "Problém prívodu paliva", "Chyba snímača"),
            recommendationEn = "Check intake leaks and compare both bank trim values.",
            recommendationSk = "Skontrolujte netesnosti sania a porovnajte korekcie oboch bánk."
        ),
        "P0175" to DtcEntry(
            en = "System too rich (Bank 2)",
            sk = "Príliš bohatá zmes (Banka 2)",
            severity = DtcSeverity.WARNING,
            causesEn = listOf("Injector issue", "Sensor error", "Fuel pressure too high"),
            causesSk = listOf("Problém vstrekovača", "Chyba snímača", "Príliš vysoký tlak paliva"),
            recommendationEn = "Check fueling balance and sensor readings on Bank 2.",
            recommendationSk = "Skontrolujte dávkovanie paliva a snímače na Banke 2."
        ),
        "P0300" to DtcEntry(
            en = "Random/multiple cylinder misfire detected",
            sk = "Náhodné/viacnásobné vynechávanie zážihu",
            severity = DtcSeverity.CRITICAL,
            causesEn = listOf("Ignition failure", "Fuel delivery issue", "Vacuum leak", "Compression problem"),
            causesSk = listOf("Zlyhanie zapaľovania", "Problém dodávky paliva", "Netesnosť podtlaku", "Problém kompresie"),
            recommendationEn = "Avoid heavy load driving. Inspect coils, spark plugs, injectors and compression.",
            recommendationSk = "Vyhnite sa jazde pod veľkým zaťažením. Skontrolujte cievky, sviečky, vstrekovače a kompresiu."
        ),
        "P0301" to DtcEntry(
            en = "Cylinder 1 misfire detected",
            sk = "Vynechávanie zážihu valca 1",
            severity = DtcSeverity.CRITICAL,
            causesEn = listOf("Faulty coil", "Worn spark plug", "Injector issue", "Low compression"),
            causesSk = listOf("Chybná cievka", "Opotrebovaná sviečka", "Problém vstrekovača", "Nízka kompresia"),
            recommendationEn = "Swap ignition components with another cylinder and inspect plug and injector.",
            recommendationSk = "Prehoďte zapaľovacie komponenty s iným valcom a skontrolujte sviečku aj vstrekovač."
        ),
        "P0302" to DtcEntry(
            en = "Cylinder 2 misfire detected",
            sk = "Vynechávanie zážihu valca 2",
            severity = DtcSeverity.CRITICAL,
            causesEn = listOf("Faulty coil", "Worn spark plug", "Injector issue", "Low compression"),
            causesSk = listOf("Chybná cievka", "Opotrebovaná sviečka", "Problém vstrekovača", "Nízka kompresia"),
            recommendationEn = "Inspect ignition, fueling and compression for cylinder 2.",
            recommendationSk = "Skontrolujte zapaľovanie, palivo a kompresiu valca 2."
        ),
        "P0303" to DtcEntry(
            en = "Cylinder 3 misfire detected",
            sk = "Vynechávanie zážihu valca 3",
            severity = DtcSeverity.CRITICAL,
            causesEn = listOf("Faulty coil", "Worn spark plug", "Injector issue", "Low compression"),
            causesSk = listOf("Chybná cievka", "Opotrebovaná sviečka", "Problém vstrekovača", "Nízka kompresia"),
            recommendationEn = "Inspect ignition, fueling and compression for cylinder 3.",
            recommendationSk = "Skontrolujte zapaľovanie, palivo a kompresiu valca 3."
        ),
        "P0304" to DtcEntry(
            en = "Cylinder 4 misfire detected",
            sk = "Vynechávanie zážihu valca 4",
            severity = DtcSeverity.CRITICAL,
            causesEn = listOf("Faulty coil", "Worn spark plug", "Injector issue", "Low compression"),
            causesSk = listOf("Chybná cievka", "Opotrebovaná sviečka", "Problém vstrekovača", "Nízka kompresia"),
            recommendationEn = "Inspect ignition, fueling and compression for cylinder 4.",
            recommendationSk = "Skontrolujte zapaľovanie, palivo a kompresiu valca 4."
        ),
        "P0351" to DtcEntry(
            en = "Ignition coil A primary/secondary circuit",
            sk = "Primárny/sekundárny obvod zapaľovacej cievky A",
            severity = DtcSeverity.CRITICAL,
            causesEn = listOf("Failed ignition coil", "Driver circuit issue", "Wiring fault"),
            causesSk = listOf("Zlyhaná zapaľovacia cievka", "Problém riadiaceho obvodu", "Porucha kabeláže"),
            recommendationEn = "Inspect coil power, ground and control signal.",
            recommendationSk = "Skontrolujte napájanie, zem a riadiaci signál cievky."
        ),
        "P0352" to DtcEntry(
            en = "Ignition coil B primary/secondary circuit",
            sk = "Primárny/sekundárny obvod zapaľovacej cievky B",
            severity = DtcSeverity.CRITICAL,
            causesEn = listOf("Failed ignition coil", "Driver circuit issue", "Wiring fault"),
            causesSk = listOf("Zlyhaná zapaľovacia cievka", "Problém riadiaceho obvodu", "Porucha kabeláže"),
            recommendationEn = "Inspect coil power, ground and control signal.",
            recommendationSk = "Skontrolujte napájanie, zem a riadiaci signál cievky."
        ),
        "P0335" to DtcEntry(
            en = "Crankshaft position sensor A circuit",
            sk = "Obvod snímača polohy kľukového hriadeľa A",
            severity = DtcSeverity.CRITICAL,
            causesEn = listOf("Crank sensor failure", "Wiring damage", "Signal loss"),
            causesSk = listOf("Porucha snímača kľukového hriadeľa", "Poškodenie kabeláže", "Strata signálu"),
            recommendationEn = "Inspect crankshaft sensor signal and wiring immediately.",
            recommendationSk = "Okamžite skontrolujte signál snímača kľukového hriadeľa a kabeláž."
        ),
        "P0340" to DtcEntry(
            en = "Camshaft position sensor circuit",
            sk = "Obvod snímača polohy vačkového hriadeľa",
            severity = DtcSeverity.WARNING,
            causesEn = listOf("Cam sensor failure", "Timing issue", "Wiring fault"),
            causesSk = listOf("Porucha snímača vačky", "Problém časovania", "Porucha kabeláže"),
            recommendationEn = "Inspect camshaft sensor signal, timing correlation and harness.",
            recommendationSk = "Skontrolujte signál snímača vačky, súlad časovania a zväzok vodičov."
        ),
        "P0401" to DtcEntry(
            en = "Exhaust gas recirculation flow insufficient detected",
            sk = "Nedostatočný prietok recirkulácie výfukových plynov",
            severity = DtcSeverity.INFO,
            causesEn = listOf("Clogged EGR passage", "Stuck EGR valve", "Vacuum control issue"),
            causesSk = listOf("Upchatý EGR kanál", "Zaseknutý EGR ventil", "Problém podtlakového ovládania"),
            recommendationEn = "Inspect EGR valve movement and carbon buildup.",
            recommendationSk = "Skontrolujte pohyb EGR ventilu a usadeniny uhlíka."
        ),
        "P0420" to DtcEntry(
            en = "Catalyst system efficiency below threshold (Bank 1)",
            sk = "Nízka účinnosť katalyzátora (Banka 1)",
            severity = DtcSeverity.INFO,
            causesEn = listOf("Aging catalyst", "Exhaust leak", "Faulty O2 sensor", "Persistent fueling issue"),
            causesSk = listOf("Starnúci katalyzátor", "Netesnosť výfuku", "Chybná lambda sonda", "Dlhodobý problém zmesi"),
            recommendationEn = "Check for exhaust leaks and O2 sensor behavior before replacing the catalyst.",
            recommendationSk = "Pred výmenou katalyzátora skontrolujte netesnosti výfuku a správanie lambda sondy."
        ),
        "P0430" to DtcEntry(
            en = "Catalyst system efficiency below threshold (Bank 2)",
            sk = "Nízka účinnosť katalyzátora (Banka 2)",
            severity = DtcSeverity.INFO,
            causesEn = listOf("Aging catalyst", "Exhaust leak", "O2 sensor fault"),
            causesSk = listOf("Starnúci katalyzátor", "Netesnosť výfuku", "Porucha lambda sondy"),
            recommendationEn = "Inspect Bank 2 catalyst efficiency and sensor switching behavior.",
            recommendationSk = "Skontrolujte účinnosť katalyzátora Banky 2 a prepínanie snímačov."
        ),
        "P0440" to DtcEntry(
            en = "Evaporative emission control system malfunction",
            sk = "Porucha systému EVAP",
            severity = DtcSeverity.INFO,
            causesEn = listOf("EVAP leak", "Purge valve issue", "Fuel cap problem"),
            causesSk = listOf("Netesnosť EVAP", "Problém purge ventilu", "Problém viečka nádrže"),
            recommendationEn = "Check the fuel cap seal and EVAP hoses first.",
            recommendationSk = "Najprv skontrolujte tesnenie viečka nádrže a hadice EVAP."
        ),
        "P0441" to DtcEntry(
            en = "EVAP incorrect purge flow",
            sk = "Nesprávny prietok preplachovania EVAP",
            severity = DtcSeverity.INFO,
            causesEn = listOf("Purge valve stuck", "Blocked line", "Control issue"),
            causesSk = listOf("Zaseknutý purge ventil", "Upchaté vedenie", "Problém ovládania"),
            recommendationEn = "Inspect purge valve operation and EVAP routing.",
            recommendationSk = "Skontrolujte činnosť purge ventilu a vedenie EVAP."
        ),
        "P0442" to DtcEntry(
            en = "EVAP small leak detected",
            sk = "Malý únik EVAP",
            severity = DtcSeverity.INFO,
            causesEn = listOf("Loose fuel cap", "Small hose leak", "Seal issue"),
            causesSk = listOf("Uvoľnené viečko nádrže", "Malý únik hadice", "Problém tesnenia"),
            recommendationEn = "Check the fuel cap and small EVAP hoses for leaks.",
            recommendationSk = "Skontrolujte viečko nádrže a malé hadice EVAP kvôli únikom."
        ),
        "P0455" to DtcEntry(
            en = "EVAP large leak detected",
            sk = "Veľký únik EVAP",
            severity = DtcSeverity.INFO,
            causesEn = listOf("Fuel cap off", "Disconnected EVAP line", "Large system leak"),
            causesSk = listOf("Chýbajúce/uvoľnené viečko nádrže", "Odpojené EVAP vedenie", "Veľký únik systému"),
            recommendationEn = "Inspect the fuel cap and all EVAP lines for disconnection.",
            recommendationSk = "Skontrolujte viečko nádrže a všetky EVAP vedenia kvôli odpojeniu."
        ),
        "P0299" to DtcEntry(
            en = "Turbo/supercharger underboost condition",
            sk = "Nízky plniaci tlak turba/kompresora",
            severity = DtcSeverity.WARNING,
            causesEn = listOf("Boost leak", "Wastegate issue", "Weak turbo", "Control solenoid problem"),
            causesSk = listOf("Netesnosť plnenia", "Problém wastegate", "Slabé turbo", "Problém riadiaceho ventilu"),
            recommendationEn = "Check boost hoses, intercooler piping and boost control hardware.",
            recommendationSk = "Skontrolujte hadice plnenia, vedenie intercoolera a prvky regulácie tlaku."
        ),
        "P0234" to DtcEntry(
            en = "Turbo/supercharger overboost condition",
            sk = "Príliš vysoký plniaci tlak turba/kompresora",
            severity = DtcSeverity.WARNING,
            causesEn = listOf("Stuck wastegate", "Boost control fault", "Sensor issue"),
            causesSk = listOf("Zaseknutý wastegate", "Porucha regulácie tlaku", "Chyba snímača"),
            recommendationEn = "Avoid heavy load. Inspect boost control hardware and pressure readings.",
            recommendationSk = "Vyhnite sa veľkému zaťaženiu. Skontrolujte reguláciu plniaceho tlaku a tlakové údaje."
        ),
        "U0100" to DtcEntry(
            en = "Lost communication with ECM/PCM",
            sk = "Strata komunikácie s ECM/PCM",
            severity = DtcSeverity.WARNING,
            causesEn = listOf("CAN wiring issue", "Low battery voltage", "Module fault"),
            causesSk = listOf("Problém kabeláže CAN", "Nízke napätie batérie", "Porucha modulu"),
            recommendationEn = "Check battery voltage and CAN network integrity.",
            recommendationSk = "Skontrolujte napätie batérie a integritu zbernice CAN."
        ),
        "B0020" to DtcEntry(
            en = "Passenger frontal deployment loop open",
            sk = "Otvorený obvod predného airbagu spolujazdca",
            severity = DtcSeverity.WARNING,
            causesEn = listOf("Airbag connector issue", "Clock spring/wiring issue", "Module fault"),
            causesSk = listOf("Problém konektora airbagu", "Problém krúžku/kabeláže", "Porucha modulu"),
            recommendationEn = "Inspect SRS wiring and connectors with battery disconnected.",
            recommendationSk = "Skontrolujte kabeláž a konektory SRS pri odpojenej batérii."
        )
    )

    private val severityOverrides = mapOf(
        "P0300" to DtcSeverity.CRITICAL,
        "P0301" to DtcSeverity.CRITICAL,
        "P0302" to DtcSeverity.CRITICAL,
        "P0303" to DtcSeverity.CRITICAL,
        "P0304" to DtcSeverity.CRITICAL,
        "P0351" to DtcSeverity.CRITICAL,
        "P0352" to DtcSeverity.CRITICAL,
        "P0335" to DtcSeverity.CRITICAL,
        "P0420" to DtcSeverity.INFO,
        "P0430" to DtcSeverity.INFO,
        "P0440" to DtcSeverity.INFO,
        "P0441" to DtcSeverity.INFO,
        "P0442" to DtcSeverity.INFO,
        "P0455" to DtcSeverity.INFO
    )

    private fun normalize(code: String): String = code.trim().uppercase()

    fun isGeneric(code: String): Boolean {
        val c = normalize(code)
        return when {
            c.length < 2 -> true
            c[0] !in charArrayOf('P', 'C', 'B', 'U') -> true
            c[1] == '0' -> true
            else -> false
        }
    }

    private fun defaultSeverity(code: String): DtcSeverity {
        val c = normalize(code)
        severityOverrides[c]?.let { return it }

        return when {
            c.startsWith("P03") -> DtcSeverity.CRITICAL
            c.startsWith("P01") || c.startsWith("P02") -> DtcSeverity.WARNING
            c.startsWith("P04") -> DtcSeverity.INFO
            c.startsWith("P0299") || c.startsWith("P0234") -> DtcSeverity.WARNING
            c.startsWith("U") -> DtcSeverity.WARNING
            c.startsWith("B") || c.startsWith("C") -> DtcSeverity.WARNING
            else -> DtcSeverity.INFO
        }
    }

    private fun fallbackDescription(code: String, language: AppLanguage): String {
        val c = normalize(code)

        val system = when (c.firstOrNull()) {
            'P' -> if (language == AppLanguage.SLOVAK) "Pohon" else "Powertrain"
            'C' -> if (language == AppLanguage.SLOVAK) "Podvozok" else "Chassis"
            'B' -> if (language == AppLanguage.SLOVAK) "Karoséria" else "Body"
            'U' -> if (language == AppLanguage.SLOVAK) "Sieť" else "Network"
            else -> if (language == AppLanguage.SLOVAK) "Neznámy systém" else "Unknown system"
        }

        val hint = when {
            c.startsWith("P01") -> if (language == AppLanguage.SLOVAK) "Nasávanie/MAF/IAT" else "Intake/MAF/IAT area"
            c.startsWith("P02") -> if (language == AppLanguage.SLOVAK) "Palivo/vstrekovanie" else "Fueling/injection area"
            c.startsWith("P03") -> if (language == AppLanguage.SLOVAK) "Vynechávanie zážihu" else "Misfire area"
            c.startsWith("P04") -> if (language == AppLanguage.SLOVAK) "EVAP/EGR" else "EVAP/EGR area"
            c.startsWith("P05") -> if (language == AppLanguage.SLOVAK) "Voľnobeh/pomocné systémy" else "Idle/auxiliary controls"
            c.startsWith("P0A") -> if (language == AppLanguage.SLOVAK) "Hybrid/EV systém" else "Hybrid/EV system"
            c.startsWith("U01") -> if (language == AppLanguage.SLOVAK) "Komunikácia riadiacich jednotiek" else "Module communication"
            else -> null
        }

        val typeText = if (isGeneric(c)) {
            if (language == AppLanguage.SLOVAK) "generický kód" else "generic code"
        } else {
            if (language == AppLanguage.SLOVAK) "výrobcom špecifický kód" else "manufacturer-specific code"
        }

        return if (hint != null) "$system: $hint ($typeText)" else "$system ($typeText)"
    }

    private fun fallbackCauses(code: String, language: AppLanguage): List<String> {
        val c = normalize(code)
        return when {
            c.startsWith("P01") -> if (language == AppLanguage.SLOVAK)
                listOf("Chyba snímača", "Netesnosť sania", "Problém kabeláže")
            else
                listOf("Sensor fault", "Intake leak", "Wiring issue")

            c.startsWith("P02") -> if (language == AppLanguage.SLOVAK)
                listOf("Problém paliva", "Vstrekovač", "Nesprávny tlak paliva")
            else
                listOf("Fueling problem", "Injector issue", "Incorrect fuel pressure")

            c.startsWith("P03") -> if (language == AppLanguage.SLOVAK)
                listOf("Zapaľovanie", "Sviečka/cievka", "Kompresia")
            else
                listOf("Ignition problem", "Spark plug/coil", "Compression issue")

            c.startsWith("P04") -> if (language == AppLanguage.SLOVAK)
                listOf("EVAP/EGR komponent", "Netesnosť", "Porucha ventilu")
            else
                listOf("EVAP/EGR component", "Leak", "Valve fault")

            c.startsWith("U") -> if (language == AppLanguage.SLOVAK)
                listOf("Komunikačná chyba", "Nízke napätie batérie", "Chyba kabeláže CAN")
            else
                listOf("Communication issue", "Low battery voltage", "CAN wiring problem")

            else -> if (language == AppLanguage.SLOVAK)
                listOf("Vyžaduje detailnejšiu diagnostiku")
            else
                listOf("Requires deeper diagnosis")
        }
    }

    private fun fallbackRecommendation(code: String, language: AppLanguage): String? {
        val c = normalize(code)
        return when {
            c.startsWith("P03") -> if (language == AppLanguage.SLOVAK)
                "Vyhnite sa vysokej záťaži motora a skontrolujte zapaľovanie čo najskôr."
            else
                "Avoid heavy engine load and inspect the ignition system as soon as possible."

            c.startsWith("U") -> if (language == AppLanguage.SLOVAK)
                "Skontrolujte napätie batérie a komunikačnú zbernicu CAN."
            else
                "Check battery voltage and the CAN communication bus."

            else -> if (language == AppLanguage.SLOVAK)
                "Skontrolujte súvisiace snímače, kabeláž a živé údaje."
            else
                "Inspect related sensors, wiring and live data."
        }
    }

    fun meta(code: String, language: AppLanguage): DtcMeta {
        val c = normalize(code)
        val entry = entries[c]

        val description = when {
            entry == null -> fallbackDescription(c, language)
            language == AppLanguage.SLOVAK -> entry.sk
            else -> entry.en
        }

        val severity = entry?.severity ?: defaultSeverity(c)

        val causes = when {
            entry == null -> fallbackCauses(c, language)
            language == AppLanguage.SLOVAK -> if (entry.causesSk.isNotEmpty()) entry.causesSk else entry.causesEn
            else -> if (entry.causesEn.isNotEmpty()) entry.causesEn else entry.causesSk
        }

        val recommendation = when {
            entry == null -> fallbackRecommendation(c, language)
            language == AppLanguage.SLOVAK -> entry.recommendationSk ?: entry.recommendationEn
            else -> entry.recommendationEn ?: entry.recommendationSk
        }

        return DtcMeta(
            code = c,
            description = description,
            severity = severity,
            likelyCauses = causes,
            recommendation = recommendation,
            isGeneric = isGeneric(c)
        )
    }
}