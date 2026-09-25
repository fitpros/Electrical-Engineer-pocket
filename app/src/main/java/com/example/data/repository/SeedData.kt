package com.example.data.repository

import com.example.data.model.AssetPhotoEntity
import com.example.data.model.AssetStatus
import com.example.data.model.CompanySettingsEntity
import com.example.data.model.CriticalityLevel
import com.example.data.model.EquipmentCategory
import com.example.data.model.EquipmentEntity
import com.example.data.model.FaultRecordEntity
import com.example.data.model.FaultSeverity
import com.example.data.model.FaultStatus
import com.example.data.model.MaintenanceStatus
import com.example.data.model.MaintenanceTaskEntity
import com.example.data.model.MaintenanceType
import com.example.data.model.PriorityLevel
import com.example.data.model.TestRecordEntity
import com.example.data.model.TestResultStatus
import com.example.data.model.TestType

object SeedData {
    val initialSettings = CompanySettingsEntity(
        id = 1,
        companyName = "Apex Power & Engineering Solutions Ltd.",
        currentProject = "Al-Khaleej 132/33 kV Substation Modernization",
        clientName = "National Electric Grid Authority",
        engineerName = "Marcus Vance, PE",
        engineerLicense = "PE-EL-98402 / Senior Protection & Commissioning Engineer",
        companyAddress = "Engineering Tower, Technopark District, Industrial Zone 4",
        contactEmail = "m.vance@apex-power-eng.com",
        reportHeader = "HIGH VOLTAGE SUBSTATION ASSET COMMISSIONING & TEST RECORD",
        reportFooter = "Certified compliance according to IEEE C37.2, IEC 60076, and IEC 60255",
        themeMode = "SYSTEM"
    )

    val initialEquipment = listOf(
        EquipmentEntity(
            id = 1L,
            tagNumber = "132-TR-01",
            name = "132/33 kV 40 MVA Main Power Transformer",
            category = EquipmentCategory.TRANSFORMER,
            location = "Al-Khaleej Substation Yard, Bay 2",
            voltageLevel = "132 kV / 33 kV",
            rating = "40 MVA ONAN / 50 MVA ONAF",
            manufacturer = "Siemens Energy TrafoSite",
            model = "T-40/132-33-YNd11",
            serialNumber = "TRAFO-2021-9841",
            commissioningDate = "2021-08-15",
            status = AssetStatus.NORMAL,
            criticality = CriticalityLevel.CRITICAL,
            specificationsJson = "{\"Vector Group\":\"Dyn11\",\"Impedance %Z\":\"12.45%\",\"BIL Rating\":\"650 kV / 170 kV\",\"Cooling\":\"ONAN / ONAF\",\"Total Weight\":\"68,500 kg\",\"Oil Capacity\":\"18,200 L Mineral Oil IEC 60296\",\"OLTC\":\"MR Reinhausen Vacuum Type VV III\",\"No Load Losses\":\"22.4 kW\",\"Full Load Losses\":\"168 kW\"}",
            photoSearchQuery = "power transformer substation industrial 40 MVA bushings conservator",
            lastInspectionDate = "2026-09-10",
            nextMaintenanceDate = "2026-10-15"
        ),
        EquipmentEntity(
            id = 2L,
            tagNumber = "33-CB-102",
            name = "33 kV 1250A Vacuum Circuit Breaker Switchgear",
            category = EquipmentCategory.CIRCUIT_BREAKER,
            location = "Indoor Switchgear Building, Room A, Bay 4",
            voltageLevel = "33 kV",
            rating = "1250 A Continuous, 31.5 kA Breaking",
            manufacturer = "ABB UniGear",
            model = "UniGear ZS1 - VD4 Mechanism",
            serialNumber = "ABB-VCB-33-0941",
            commissioningDate = "2022-03-20",
            status = AssetStatus.WARNING,
            criticality = CriticalityLevel.HIGH,
            specificationsJson = "{\"Rated Voltage\":\"36 kV\",\"Rated Current\":\"1250 A\",\"Short Circuit Breaking\":\"31.5 kA (3s)\",\"Making Current\":\"80 kA Peak\",\"Operating Sequence\":\"O - 0.3s - CO - 3min - CO\",\"Control Voltage\":\"110 V DC\",\"Insulation Medium\":\"Air insulated with Vacuum interrupter\",\"Interrupter Type\":\"ABB VG4\",\"Enclosure Protection\":\"IP4X\"}",
            photoSearchQuery = "33 kV switchgear vacuum circuit breaker VCB indoor panel",
            lastInspectionDate = "2026-09-02",
            nextMaintenanceDate = "2026-09-28"
        ),
        EquipmentEntity(
            id = 3L,
            tagNumber = "615-RY-04",
            name = "Feeder Protection & Control IED Relay",
            category = EquipmentCategory.PROTECTION_RELAY,
            location = "Relay & SCADA Control Room, Panel CP-04",
            voltageLevel = "33 kV Control",
            rating = "1A/5A CT, 110V VT, 110V DC Aux",
            manufacturer = "ABB Relion",
            model = "REF615 (Standard Configuration F)",
            serialNumber = "REF-2023-5512",
            commissioningDate = "2023-01-10",
            status = AssetStatus.NORMAL,
            criticality = CriticalityLevel.CRITICAL,
            specificationsJson = "{\"Protection Functions\":\"50/51 (Overcurrent), 50N/51N (Earth Fault), 49 (Thermal), 67 (Directional), 27/59 (Under/Over Voltage), 81 (Frequency), 50BF (Breaker Failure)\",\"Communication\":\"IEC 61850 Edition 2 with GOOSE, Modbus TCP/IP\",\"Binary Inputs\":\"16 Optoisolated\",\"Binary Outputs\":\"10 (including High Speed Trip)\",\"HMI\":\"Full graphic LC display with single-line diagram mimic\"}",
            photoSearchQuery = "ABB REF615 electrical protection relay panel substation IED",
            lastInspectionDate = "2026-08-18",
            nextMaintenanceDate = "2026-11-20"
        ),
        EquipmentEntity(
            id = 4L,
            tagNumber = "33-CBL-08",
            name = "33 kV 3-Core 240mm² Cu XLPE Feeder Cable",
            category = EquipmentCategory.CABLE,
            location = "Underground Cable Trench Route Bay 4 to Plant Bus",
            voltageLevel = "33 kV (19/33 kV)",
            rating = "420 A In-Ground, 2.4 km Run",
            manufacturer = "Prysmian Group",
            model = "Cu/XLPE/CWS/PVC/AWA/MDPE",
            serialNumber = "CBL-PRY-33-240",
            commissioningDate = "2021-09-01",
            status = AssetStatus.NORMAL,
            criticality = CriticalityLevel.HIGH,
            specificationsJson = "{\"Conductor Material\":\"Compacted Plain Stranded Copper\",\"Cross Section\":\"240 mm²\",\"Conductor Screen\":\"Semi-conducting compound\",\"Insulation\":\"Dry-cured XLPE (8.0 mm nominal)\",\"Metallic Screen\":\"Copper wire screen 35 mm²\",\"Armour\":\"Aluminium wire armour (AWA)\",\"AC Resistance @ 90°C\":\"0.098 Ω/km\",\"Reactance @ 50Hz\":\"0.112 Ω/km\",\"Capacitance\":\"0.24 µF/km\"}",
            photoSearchQuery = "high voltage cable termination joint 33kV XLPE tray substation",
            lastInspectionDate = "2026-07-25",
            nextMaintenanceDate = "2026-10-05"
        ),
        EquipmentEntity(
            id = 5L,
            tagNumber = "MTR-PMP-12",
            name = "450 kW 3.3 kV Boiler Feed Pump Motor",
            category = EquipmentCategory.MOTOR,
            location = "Pumping Station Building 2, Skid Pump A",
            voltageLevel = "3.3 kV 3-Phase",
            rating = "450 kW, 96.5 A, 1485 RPM",
            manufacturer = "WEG Electric Corp",
            model = "W22Xdb Flameproof MV Induction Motor",
            serialNumber = "WEG-2020-8812",
            commissioningDate = "2020-11-12",
            status = AssetStatus.NORMAL,
            criticality = CriticalityLevel.HIGH,
            specificationsJson = "{\"Rated Power\":\"450 kW (600 HP)\",\"Efficiency\":\"96.8% (IE3 Premium)\",\"Power Factor\":\"0.88\",\"Starting Current Ratio\":\"6.8 x In\",\"Starting Method\":\"Soft Starter with VFD Bypass\",\"Insulation Class\":\"Class F (Temp Rise B)\",\"Enclosure / Cooling\":\"TEFC - IC411, IP66\",\"Hazardous Classification\":\"ATEX Zone 1 Ex db IIC T4 Gb\",\"Bearing Type\":\"Insulated DE & NDE Roller Bearings\"}",
            photoSearchQuery = "heavy industrial electrical motor control center VFD pump skid",
            lastInspectionDate = "2026-09-12",
            nextMaintenanceDate = "2026-10-30"
        ),
        EquipmentEntity(
            id = 6L,
            tagNumber = "PV-INV-SKID-01",
            name = "2.5 MW 1500V DC Utility-Scale Solar Inverter Skid",
            category = EquipmentCategory.SOLAR_PV,
            location = "Solar Field Sector Alpha, Pad 1",
            voltageLevel = "1500 V DC / 690 V AC / 33 kV Skid",
            rating = "2500 kVA @ 50°C, 33 kV Step-Up",
            manufacturer = "SMA Solar Technology",
            model = "Sunny Central 2500-EV Compact Skid",
            serialNumber = "SMA-SC2500-0198",
            commissioningDate = "2023-05-18",
            status = AssetStatus.NORMAL,
            criticality = CriticalityLevel.MEDIUM,
            specificationsJson = "{\"Max DC Input Voltage\":\"1500 V\",\"MPPT Voltage Range\":\"850 V - 1425 V\",\"Max DC Current\":\"3200 A\",\"Max Euro Efficiency\":\"98.7%\",\"Grid Nominal Output\":\"690 V AC stepped to 33 kV\",\"Integrated Transformer\":\"2750 kVA 0.69/33 kV Cast Resin\",\"Protections\":\"DC & AC Surge Class II, Ground fault monitoring, Anti-Islanding IEEE 1547\",\"Communication\":\"Modbus RTU/TCP, SunSpec, SCADA interface\"}",
            photoSearchQuery = "utility scale solar hybrid inverter PV array substation MV skid",
            lastInspectionDate = "2026-08-30",
            nextMaintenanceDate = "2026-11-15"
        )
    )

    val initialPhotos = listOf(
        AssetPhotoEntity(
            equipmentId = 1L,
            photoUri = "",
            caption = "Main Rating & Nameplate (Siemens 40/50 MVA Dyn11)",
            photoType = "Nameplate",
            dateAdded = "2026-09-10",
            inspectionRef = "INS-2026-09-TR01"
        ),
        AssetPhotoEntity(
            equipmentId = 1L,
            photoUri = "",
            caption = "132 kV High Voltage Bushings & Surge Arresters",
            photoType = "Front View",
            dateAdded = "2026-09-10",
            inspectionRef = "INS-2026-09-TR01"
        ),
        AssetPhotoEntity(
            equipmentId = 2L,
            photoUri = "",
            caption = "Front Cassette Interrupter & Mechanical Indicator (Open/Closed)",
            photoType = "Front View",
            dateAdded = "2026-09-02",
            inspectionRef = "INS-2026-09-CB102"
        ),
        AssetPhotoEntity(
            equipmentId = 3L,
            photoUri = "",
            caption = "REF615 Active MIMIC Display & Protection Status LEDs",
            photoType = "Relay Display",
            dateAdded = "2026-08-18",
            inspectionRef = "INS-2026-08-RY04"
        )
    )

    val initialTasks = listOf(
        MaintenanceTaskEntity(
            id = 1L,
            equipmentId = 1L,
            equipmentTag = "132-TR-01",
            equipmentName = "Main Power Transformer 40 MVA",
            title = "Buchholz Relay Gas Sampling & Silica Gel Desiccant Check",
            taskType = MaintenanceType.PREVENTIVE,
            priority = PriorityLevel.URGENT,
            dueDate = "2026-09-25", // Due Today
            status = MaintenanceStatus.DUE_TODAY,
            assignedEngineer = "Marcus Vance, PE",
            notes = "Inspect oil conservator level, check color of desiccant crystals (pink indicates saturation), bleed air from Buchholz petcock.",
            checklistItems = "Verify oil level in conservator;Check silica gel colour (minimum 75% blue/amber active);Inspect Buchholz gas accumulation window;Check for leaks around bushing seals;Record ambient and oil temperature gauges"
        ),
        MaintenanceTaskEntity(
            id = 2L,
            equipmentId = 2L,
            equipmentTag = "33-CB-102",
            equipmentName = "33 kV VCB Switchgear",
            title = "Vacuum Bottle Contact Erosion & Mechanism Timing Test",
            taskType = MaintenanceType.PREVENTIVE,
            priority = PriorityLevel.HIGH,
            dueDate = "2026-09-28", // Due this week
            status = MaintenanceStatus.DUE_THIS_WEEK,
            assignedEngineer = "Fahad Al-Mansoor",
            notes = "Measure contact wipe gap, verify closing time (< 65ms) and opening time (< 45ms), lubricate spring charging linkages.",
            checklistItems = "Isolate breaker and apply safety earthing;Verify vacuum bottle wear indicators;Measure primary contact resistance with 100A Ductor;Test motor spring charging time (< 12 seconds);Verify auxiliary switch contact continuity"
        ),
        MaintenanceTaskEntity(
            id = 3L,
            equipmentId = 4L,
            equipmentTag = "33-CBL-08",
            equipmentName = "33 kV 240mm² Feeder Cable",
            title = "VLF Tan-Delta & Partial Discharge Diagnostic Screening",
            taskType = MaintenanceType.PREDICTIVE,
            priority = PriorityLevel.HIGH,
            dueDate = "2026-09-15", // Overdue
            status = MaintenanceStatus.OVERDUE,
            assignedEngineer = "David K. O'Connor",
            notes = "Apply 0.1Hz VLF sinusoidal test up to 2U0 (38 kV RMS) to detect water treeing and micro-voids in cable insulation.",
            checklistItems = "Permit to Work and Lockout/Tagout confirmed;Disconnect cable terminations at both substations;Execute VLF Tan Delta measurement at 0.5U0, 1.0U0, 1.5U0, 2.0U0;Record differential Tan Delta (delta-TD);Perform post-test solid discharge and ground"
        ),
        MaintenanceTaskEntity(
            id = 4L,
            equipmentId = 3L,
            equipmentTag = "615-RY-04",
            equipmentName = "REF615 Protection Relay",
            title = "Secondary Current Injection & ANSI 50/51 Verification",
            taskType = MaintenanceType.COMMISSIONING,
            priority = PriorityLevel.MEDIUM,
            dueDate = "2026-08-18",
            completionDate = "2026-08-18",
            status = MaintenanceStatus.COMPLETED,
            assignedEngineer = "Marcus Vance, PE",
            notes = "All 3 phases injected using Omicron CMC 356. Pick-up at 1.05 A, tripping times matched IEC Normal Inverse within 1.2% tolerance.",
            checklistItems = "Verify CT secondary grounding integrity;Inject 2x, 3x, 5x Is on Phase A, B, C;Check breaker trip coil initiation contacts;Verify SCADA Modbus event timestamping"
        ),
        MaintenanceTaskEntity(
            id = 5L,
            equipmentId = 5L,
            equipmentTag = "MTR-PMP-12",
            equipmentName = "450 kW Boiler Feed Pump Motor",
            title = "Vibration Spectrum Analysis & Stator RTD Logging",
            taskType = MaintenanceType.PREDICTIVE,
            priority = PriorityLevel.LOW,
            dueDate = "2026-09-12",
            completionDate = "2026-09-12",
            status = MaintenanceStatus.COMPLETED,
            assignedEngineer = "K. Sen",
            notes = "Overall vibration RMS: 1.8 mm/s DE, 1.4 mm/s NDE (Zone A - ISO 10816 compliant). Winding temperatures nominal at 82°C.",
            checklistItems = "Radial and axial accelerometer readings;Spectrum FFT 1x, 2x running speed harmonics;Inspect lubrication grease condition;Check RTD PT100 wiring continuity"
        )
    )

    val initialFaults = listOf(
        FaultRecordEntity(
            id = 1L,
            equipmentId = 2L,
            equipmentTag = "33-CB-102",
            equipmentName = "33 kV VCB Switchgear",
            equipmentCategory = EquipmentCategory.CIRCUIT_BREAKER,
            title = "Feeder Phase B-C Short Circuit Trip with Breaker Lockout",
            timestamp = "2026-09-22 04:12:18 UTC",
            severity = FaultSeverity.PROTECTION_TRIP,
            status = FaultStatus.ROOT_CAUSE_IDENTIFIED,
            tripRelayFunction = "ANSI 50/51 Phase Overcurrent & 86 Master Lockout",
            breakerStatus = "Tripped / Open (Locked Out by 86 Relay)",
            observedInfo = "Feeder 4 tripped instantaneously on heavy fault current. REF615 IED recorded Phase B: 4,120 A, Phase C: 3,980 A. Bus voltage dipped to 68% for 82 ms. High acoustic discharge reported near cable basement entry.",
            possibleCauses = "1. Cold-shrink cable termination stress cone breakdown\n2. External animal intrusion across un-insulated indoor busbar support\n3. Flashover across contaminated CT support insulators\n4. Internal vacuum interrupter bottle flashover",
            measurementsFindings = "Insulation resistance of Cable 33-CBL-08:\n- Phase A to Earth: 4.8 GΩ (Healthy)\n- Phase B to Earth: 0.12 MΩ (FAILED - Direct breakdown)\n- Phase C to Earth: 0.18 MΩ (FAILED - Direct breakdown)\n- Phase B to C: 0.04 MΩ (Flashover track identified)\nVisual inspection located burnt carbon tracks on stress cone 150mm above cable gland.",
            rootCause = "Premature moisture ingress and void discharge inside cold-shrink termination stress cone due to improper mastic sealant application during 2021 installation.",
            correctiveAction = "1. Cut back damaged 600mm cable end.\n2. Re-terminate using 3M Raychem heat-shrink termination kit with double mastic sealing.\n3. Perform high potential DC insulation test prior to re-energizing.\n4. Reset ANSI 86 lockout."
        ),
        FaultRecordEntity(
            id = 2L,
            equipmentId = 1L,
            equipmentTag = "132-TR-01",
            equipmentName = "Main Power Transformer 40 MVA",
            equipmentCategory = EquipmentCategory.TRANSFORMER,
            title = "Winding Temperature Stage 1 Alarm & ONAF Cooling Anomaly",
            timestamp = "2026-09-18 13:45:00 UTC",
            severity = FaultSeverity.CRITICAL_ALARM,
            status = FaultStatus.RESOLVED,
            tripRelayFunction = "ANSI 49 Thermal Overload Alarm (Stage 1 @ 95°C)",
            breakerStatus = "Closed / In Service (Operating Under Load Restriction)",
            observedInfo = "SCADA displayed audible alarm: 132-TR-01 WTI (Winding Temp Indicator) reached 96°C at 74% loading during peak noon ambient (43°C). Radiator fan bank B failed to start automatically.",
            possibleCauses = "1. Fan motor starter overload trip\n2. 110V DC auxiliary control circuit blown fuse\n3. WTI mechanical mercury switch / micro-switch misalignment\n4. Radiator oil circulation valve partially shut",
            measurementsFindings = "Voltage at cooling cabinet incoming terminals: 400V AC nominal. Control coil voltage measured: 110V DC present when simulated manually. Radiator Fan Bank 2 contactor (KM-02) coil open circuit (burned coil). Oil valves fully open in position.",
            rootCause = "Defective 230V AC / 110V DC coil insulation on Fan Bank 2 magnetic contactor caused thermal burnout and failure to engage cooling fans.",
            correctiveAction = "Replaced contactor KM-02 with heavy-duty Schneider TeSys D 32A unit. Manually forced and verified all 6 radiator fans. WTI dropped to 78°C within 35 minutes."
        )
    )

    val initialTests = listOf(
        TestRecordEntity(
            id = 1L,
            equipmentId = 1L,
            equipmentTag = "132-TR-01",
            equipmentName = "132/33 kV Power Transformer",
            testType = TestType.INSULATION_RESISTANCE,
            testDate = "2026-09-10",
            ambientTempC = 28.5,
            humidityPercent = 46.0,
            instrumentModel = "Megger MIT525 (5 kV Digital Insulation Tester)",
            instrumentCalibDate = "2026-01-15 (Valid)",
            testResult = TestResultStatus.PASS,
            summaryFindings = "All polarization index (PI) values exceed 2.0 (IEEE 43 standard requirement is > 1.5). No degradation in transformer oil or solid cellulose paper insulation.",
            readingsJson = "{\"HV to LV + Earth (5kV @ 10m)\":\"14.8 GΩ\",\"HV to LV + Earth (1m / 10m PI)\":\"2.42 (Pass)\",\"LV to HV + Earth (2.5kV @ 10m)\":\"8.9 GΩ\",\"LV to HV + Earth (1m / 10m PI)\":\"2.15 (Pass)\",\"HV to LV Guarded (5kV)\":\"28.4 GΩ\"}",
            engineerName = "Marcus Vance, PE",
            clientWitness = "Eng. Tariq Al-Suwaidi (Grid Auth)"
        ),
        TestRecordEntity(
            id = 2L,
            equipmentId = 2L,
            equipmentTag = "33-CB-102",
            equipmentName = "33 kV VCB Switchgear",
            testType = TestType.CONTACT_RESISTANCE,
            testDate = "2026-09-02",
            ambientTempC = 30.0,
            humidityPercent = 52.0,
            instrumentModel = "Megger MOM2 (200A Micro-ohmmeter)",
            instrumentCalibDate = "2026-02-10 (Valid)",
            testResult = TestResultStatus.PASS,
            summaryFindings = "Primary pole contact resistance within manufacturer limit (< 35 µΩ per phase). Phase deviation < 5%.",
            readingsJson = "{\"Pole A Contact Resistance @ 100A DC\":\"27.4 µΩ\",\"Pole B Contact Resistance @ 100A DC\":\"28.1 µΩ\",\"Pole C Contact Resistance @ 100A DC\":\"26.8 µΩ\",\"Manufacturer Limit\":\"< 35.0 µΩ\",\"Result\":\"Acceptable (Pass)\"}",
            engineerName = "Fahad Al-Mansoor",
            clientWitness = "Eng. Tariq Al-Suwaidi (Grid Auth)"
        ),
        TestRecordEntity(
            id = 3L,
            equipmentId = 3L,
            equipmentTag = "615-RY-04",
            equipmentName = "REF615 Protection Relay",
            testType = TestType.RELAY_TIMING,
            testDate = "2026-08-18",
            ambientTempC = 24.0,
            humidityPercent = 40.0,
            instrumentModel = "Omicron CMC 356 Universal Relay Test Set",
            instrumentCalibDate = "2026-03-01 (Valid)",
            testResult = TestResultStatus.PASS,
            summaryFindings = "IEC 60255 Normal Inverse overcurrent curve verified at TMS=0.10, Pickup=1.0A. Operating time deviation from theoretical curve < 1.5% across all test points.",
            readingsJson = "{\"Test Current 2.0x Is\":\"Expected 1.003s | Measured 1.012s (Pass)\",\"Test Current 3.0x Is\":\"Expected 0.630s | Measured 0.634s (Pass)\",\"Test Current 5.0x Is\":\"Expected 0.428s | Measured 0.431s (Pass)\",\"High-Set 50 Pick-up (10x Is)\":\"Expected 35ms | Measured 32ms (Pass)\"}",
            engineerName = "Marcus Vance, PE",
            clientWitness = "Eng. Tariq Al-Suwaidi (Grid Auth)"
        )
    )
}
