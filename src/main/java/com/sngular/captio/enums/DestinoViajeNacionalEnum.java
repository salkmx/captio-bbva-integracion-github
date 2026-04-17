package com.sngular.captio.enums;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.Locale;

public enum DestinoViajeNacionalEnum {

	ACAPULCO_KRYSTAL_BEACH_ACAPULCO(65, "Acapulco - Krystal Beach Acapulco"),
	ACAPULCO_EMPORIO_ACAPULCO(66, "Acapulco - Emporio Acapulco"),
	ACAPULCO_ONE_COSTERA_MIGUEL(67, "Acapulco - One Costera Miguel"),
	ACAPULCO_FIESTA_AMERICANA_VILLAS(68, "Acapulco - Fiesta Americana Villas"),
	AGUASCALIENTES_MARRIOTT_AGUASCALIENTES(69, "Aguascalientes - Marriott Aguascalientes"),
	AGUASCALIENTES_QUINTA_REAL_AGUASCALIENTES(70, "Aguascalientes - Quinta Real Aguascalientes"),
	AGUASCALIENTES_FIESTA_AMERICANA_AGUASCALIENTES(71, "Aguascalientes - Fiesta Americana Aguascalientes"),
	AGUASCALIENTES_HILTON_GARDEN_INNN(72, "Aguascalientes - Hilton Garden Innn"),
	AGUASCALIENTES_HYATT_PLACE_AGUASCALIENTES(73, "Aguascalientes - Hyatt Place Aguascalientes"),
	APIZACO_CITY_EXPRESS_APIZACO(74, "Apizaco - City Express Apizaco"),
	CABO_SAN_LUCAS_FAIRFIELD_INN_LOS_CABOS(75, "Cabo San Lucas - Fairfield Inn Los Cabos"),
	CABOS_SAN_JOSE_HYATT_PLACE_LOS_CABOS(76, "Cabos San José - Hyatt Place Los Cabos"),
	CAMPECHE_HOLIDAY_INN_CAMPECHE(77, "Campeche - Holiday Inn Campeche"),
	CAMPECHE_GAMMA_CAMPECHE_MALECON(78, "Campeche - Gamma Campeche Malecón"),
	CANANEA_CITY_EXPRESS_CANANEA(79, "Cananea - City Express Cananea"),
	CANCUN_EMPORIO_CANCUN(80, "Cancún - Emporio Cancún"), CANCUN_JW_MARRIOTT(81, "Cancún - JW Marriott"),
	CANCUN_HILTON_CANOPY(82, "Cancún - Hilton Canopy"),
	CANCUN_FIESTA_INN_CANCUN_CENTRO(83, "Cancún - Fiesta Inn Cancún Centro"),
	CANCUN_KRYSTAL_URBAN_CANCUN(84, "Cancún - Krystal Urban Cancún"), CANCUN_REINNASSANCE(85, "Cancún - REINNASSANCE"),
	CELAYA_CITY_EXPRESS_CELAYA_GALERIAS(86, "Celaya - City Express Celaya Galerias"),
	CELAYA_DOUBLETREE_BY_HILTON_CELAYA(87, "Celaya - DoubleTree by Hilton Celaya"),
	CELAYA_FIESTA_INN_CELAYA_GALERIAS(88, "Celaya - Fiesta Inn Celaya Galerias"),
	CELAYA_REAL_INN_CELAYA(89, "Celaya - Real Inn Celaya"),
	CELAYA_HOLIDAY_INN_EXPRESS_SUITES_CELAYA(90, "Celaya - Holiday Inn Express & Suites Celaya"),
	CHETUMAL_CITY_EXPRESS_CHETUMAL(91, "Chetumal - City Express Chetumal"),
	CHETUMAL_FIESTA_INN_CHETUMAL(92, "Chetumal - Fiesta Inn Chetumal"),
	CHIHUAHUA_COURTYARD_BY_MARRIOTT_CHIHUAHUA(93, "Chihuahua - Courtyard by Marriott Chihuahua"),
	CHIHUAHUA_HOLIDAY_INN_SUITES_CHIHUAHUA(94, "Chihuahua - Holiday Inn & Suites Chihuahua"),
	CHIHUAHUA_FIESTA_INN_CHIHUAHUA_FASHION_MALL(95, "Chihuahua - Fiesta Inn Chihuahua Fashion Mall"),
	CHIHUAHUA_FIESTA_INN_CHIHUAHUA(96, "Chihuahua - Fiesta Inn Chihuahua"),
	CHILPANCINGO_HOLIDAY_INN_CHILPANCINGO(97, "Chilpancingo - Holiday Inn Chilpancingo"),
	CDMX_GALERIA_PLAZA_REFORMA(98, "Ciudad de México - Galeria Plaza Reforma"),
	CDMX_SHERATON_MEXICO_CITY_MARIA_ISABEL(99, "Ciudad de México - Sheraton Mexico City Maria Isabel Hotel"),
	CDMX_MARRIOTT_REFORMA(100, "Ciudad de México - Marriott Reforma"),
	CDMX_BARCELO_REFORMA(101, "Ciudad de México - Barceló Reforma"),
	CDMX_LE_MERIDIEN_MEXICO_CITY(102, "Ciudad de México - Le Méridien Mexico City"),
	CDMX_CAMINO_REAL_POLANCO(103, "Ciudad de México - Camino Real Polanco"),
	CDMX_FIESTA_INN_INSURGENTES_VIADUCTO(104, "Ciudad de México - Fiesta Inn Insurgentes Viaducto"),
	CDMX_COURTYARD_MEXICO_CITY_TOREO(105, "Ciudad de México - Courtyard Mexico City Toreo"),
	CDMX_FIESTA_INN_NAUCALPAN(106, "Ciudad de México - Fiesta Inn Naucalpan"),
	CDMX_NH_REFORMA(107, "Ciudad de México - NH Reforma"),
	CDMX_WYNDHAM_POLANCO(108, "Ciudad de México - Wyndham Polanco"),
	CDMX_COURTYARD_VALLEJO(109, "Ciudad de México - Courtyard Vallejo"),
	CDMX_NOVOTEL_TOREO(110, "Ciudad de México - Novotel Toreo"),
	CDMX_HOTSSON_CDMX(111, "Ciudad de México - Hotsson CDMX"),
	CDMX_CAMINO_REAL_AEROPUERTO(112, "Ciudad de México - Camino Real Aeropuerto"),
	CDMX_FIESTA_INN_AEROPUERTO_CDMX(113, "Ciudad de México - Fiesta Inn Aeropuerto CDMX"),
	CIUDAD_DEL_CARMEN_FIESTA_INN(114, "Ciudad del Carmen - Fiesta Inn Ciudad del Carmen"),
	CIUDAD_DEL_CARMEN_HOLIDAY_INN(115, "Ciudad del Carmen - Holiday inn Cd del Carmen"),
	CIUDAD_DEL_CARMEN_HOLIDAY_INN_EXPRESS(116, "Ciudad del Carmen - Holiday Inn Express Cd. Del Carmen"),
	CIUDAD_DELICIAS_HOTEL_CASA_GRANDE(117, "Ciudad Delicias - Hotel Casa grande Delicias"),
	CIUDAD_JUAREZ_FIESTA_INN(118, "Ciudad Juárez - Fiesta Inn Ciudad Juarez"),
	CIUDAD_JUAREZ_REAL_INN(119, "Ciudad Juárez - Real Inn Ciudad Juarez"),
	CIUDAD_JUAREZ_LUCERNA_CHIHUAHUA(120, "Ciudad Juárez - Lucerna Chihuahua"),
	CIUDAD_JUAREZ_CITY_EXPRESS(121, "Ciudad Juárez - City Express Ciudad Juarez"),
	CIUDAD_JUAREZ_HOLIDAY_INN_EXPRESS_SUITES(122, "Ciudad Juárez - Holiday Inn Express & Suites Juarez"),
	CIUDAD_JUAREZ_HAMPTON_BY_HILTON(123, "Ciudad Juárez - Hampton by Hilton Ciudad Juárez"),
	CIUDAD_OBREGON_FIESTA_INN(124, "Ciudad Obregón - Fiesta Inn Ciudad Obregon"),
	CIUDAD_OBREGON_HOLIDAY_INN_EXPRESS_SUITES(125, "Ciudad Obregón - Holiday Inn Express & Suites Cd. Obregon"),
	CIUDAD_VICTORIA_ISTAY(126, "Ciudad Victoria - Istay Ciudad Victoria"),
	CIUDAD_VICTORIA_HAMPTON_INN_HILTON(127, "Ciudad Victoria - Hampton Inn Hilton"),
	COATZACOALCOS_HOLIDAY_INN(128, "Coatzacoalcos - Holiday Inn Coatzacoalcos"),
	COATZACOALCOS_FIESTA_INN(129, "Coatzacoalcos - Fiesta Inn Coatzacoalcos"),
	COLIMA_FIESTA_INN(130, "Colima - Fiesta Inn Colima"), COLIMA_GAMMA(131, "Colima - Gamma"),
	COMITAN_CITY_EXPRESS(132, "Comitán - City Express Comitán"),
	CUERNAVACA_HOLIDAY_INN(133, "Cuernavaca - Holiday Inn Cuernavaca"),
	CUERNAVACA_FIESTA_INN(134, "Cuernavaca - Fiesta Inn Cuernavaca"),
	CULIACAN_FIESTA_INN(135, "Culiacán - Fiesta Inn Culiacan"),
	CULIACAN_HOLIDAY_INN_EXPRESS(136, "Culiacán - Holiday Inn Express Culiacan"),
	CULIACAN_LUCERNA(137, "Culiacán - Lucerna Culiacán"), DURANGO_HOLIDAY_INN(138, "Durango - Holiday Inn Durango"),
	DURANGO_FIESTA_INN(139, "Durango - Fiesta Inn Durango"),
	DURANGO_HAMPTON_INN_BY_HILTON(140, "Durango - Hampton inn by Hilton"),
	ENSENADA_CITY_EXPRESS_PLUS(141, "Ensenada - City Express Plus Ensenada"),
	GUADALAJARA_HYATT_REGENCY_ANDARES(142, "Guadalajara - Hyatt Regency Andares"),
	GUADALAJARA_GRAND_FIESTA_AMERICANA_COUNTRY(143, "Guadalajara - Grand Fiesta Americana Country"),
	GUADALAJARA_NH_PROVIDENCIA(144, "Guadalajara - NH Providencia"),
	GUADALAJARA_CITY_EXPRESS_PLUS_PROVIDENCIA(145, "Guadalajara - City Express Plus Providencia"),
	GUADALAJARA_FIESTA_AMERICANA(146, "Guadalajara - Fiesta Americana Guadalajara"),
	GUADALAJARA_INTERCONTINENTAL_PRESIDENTE(147, "Guadalajara - Intercontinental Presidente Guadalajara"),
	GUADALAJARA_HOLIDAY_INN_PATRIA(148, "Guadalajara - Holiday Inn Patria"),
	GUANAJUATO_HOTEL_INDIGO(149, "Guanajuato - Hotel Indigo Guanajuato"),
	GUANAJUATO_HOLIDAY_INN_EXPRESS(150, "Guanajuato - Holiday Inn Express Guanajuato"),
	GUANAJUATO_HOTEL_MISION(151, "Guanajuato - Hotel Misión"),
	GUANAJUATO_EX_HACIENDA_SAN_XAVIER(152, "Guanajuato - Ex Hacienda San Xavier"),
	GUAYMAS_CITY_EXPRESS(153, "Guaymas - City Express Guaymas"),
	GUAYMAS_HOLIDAY_INN_EXPRESS(154, "Guaymas - Holiday Inn Express Guaymas"),
	HERMOSILLO_CITY_EXPRESS(155, "Hermosillo - City Express Hermosillo"),
	HERMOSILLO_LUCERNA(156, "Hermosillo - Lucerna Hermosillo"),
	HERMOSILLO_COURTYARD(157, "Hermosillo - Courtyard Hermosillo"),
	HERMOSILLO_FIESTA_AMERICANA(158, "Hermosillo - Fiesta Americana Hermosillo"),
	HERMOSILLO_FIESTA_INN(159, "Hermosillo - Fiesta Inn Hermosillo"),
	HUATULCO_HOLIDAY_INN(160, "Huatulco - Holiday Inn"), HUATULCO_CAMINO_REAL(161, "Huatulco - Camino Real"),
	IRAPUATO_CITY_EXPRESS(162, "Irapuato - City Express Irapuato"),
	IRAPUATO_HOTSSON(163, "Irapuato - Hotsson Irapuato"),
	IXTAPA_ZIHUATANEJO_KRYSTAL_IXTAPA(164, "Ixtapa Zihuatanejo - Krystal Ixtapa"),
	IXTAPA_ZIHUATANEJO_EMPORIO_IXTAPA(165, "Ixtapa Zihuatanejo - Emporio Ixtapa"),
	LA_PAZ_COURTYARD_BY_MARRIOTT(166, "La Paz - Courtyard By Marriott"),
	LA_PAZ_ARAIZA_PALMIRA(167, "La Paz - Araiza Pálmira La Paz"),
	LEON_COURTYARD_LEON_POLIFORUM(168, "León - Courtyard Leon at The Poliforum"),
	LEON_GALERIA_PLAZA(169, "León - Galeria Plaza León"), LEON_CROWNE_PLAZA(170, "León - Crowne Plaza León"),
	LEON_HYATT_CENTRIC(171, "León - Hyatt Centric Leon"),
	LEON_HOLIDAY_INN_SUITES_PLAZA_MAYOR(172, "León - Holiday Inn & Suites Leon Plaza Mayor"),
	LEON_HOTSSON(173, "León - Hotsson León"), LOS_MOCHIS_CITY_EXPRESS(174, "Los Mochis - City Express Los Mochis"),
	LOS_MOCHIS_FIESTA_INN(175, "Los Mochis - Fiesta Inn Los Mochis"),
	MANZANILLO_CITY_EXPRESS(176, "Manzanillo - City Express Manzanillo"),
	MANZANILLO_HOLIDAY_INN_EXPRESS(177, "Manzanillo - Holiday Inn Express Manzanillo"),
	MATAMOROS_CITY_EXPRESS(178, "Matamoros - City Express Matamoros"),
	MATAMOROS_HOLIDAY_INN(179, "Matamoros - Holiday Inn Matamoros"),
	MAZATLAN_HOLIDAY_INN_RESORT(180, "Mazatlán - Holiday Inn Resort Mazatlan"),
	MAZATLAN_DOUBLETREE_BY_HILTON(181, "Mazatlán - DoubleTree by Hilton Mazatlán"),
	MAZATLAN_EMPORIO(182, "Mazatlán - Emporio Mazatlán"),
	MAZATLAN_WYNDHAM_GARDEN(183, "Mazatlán - WYNDHAM Garden Mazatlan"),
	MAZATLAN_FIESTA_INN(184, "Mazatlán - Fiesta Inn"), MERIDA_FIESTA_AMERICANA(185, "Mérida - Fiesta Americana Merida"),
	MERIDA_CAMINO_REAL(186, "Mérida - Camino Real Mérida"), MERIDA_NH_COLLECTION(187, "Mérida - NH Collection Mérida"),
	MEXICALI_FIESTA_INN(188, "Mexicali - Fiesta Inn Mexicali"), MEXICALI_REAL_INN(189, "Mexicali - REAL INN MEXICALI"),
	MEXICALI_LUCERNA(190, "Mexicali - Lucerna Mexicali"), MEXICALI_ARAIZA(191, "Mexicali - Araiza Mexicali"),
	MONCLOVA_HOLIDAY_INN(192, "Monclova - Holiday Inn Monclova"),
	MONCLOVA_FIESTA_INN(193, "Monclova - Fiesta Inn Monclova"),
	MONTERREY_FIESTA_AMERICANA_PABELLON(194, "Monterrey - Fiesta Americana Pabellón"),
	MONTERREY_HILTON_GARDEN_INN_OBISPADO(195, "Monterrey - Hilton Garden Inn Obispado"),
	MONTERREY_NH_MONTERREY(196, "Monterrey - NH Monterrey"),
	MONTERREY_CAMINO_REAL_VALLE(197, "Monterrey - Camino Real Monterrey Valle"),
	MONTERREY_CAMINO_REAL_FASHION_DRIVE(198, "Monterrey - Camino Real Monterrey Fashion Drive"),
	MONTERREY_FIESTA_INN_VALLE(199, "Monterrey - Fiesta Inn Monterrey Valle"),
	MONTERREY_MS_MILENIUM_CURIO(200, "Monterrey - MS Milenium Monterrey, Curio Collection by Hilton"),
	MONTERREY_HOLIDAY_INN_EXPRESS_SUITES_VALLE(201, "Monterrey - Holiday Inn Express & Suites Mty. Valle"),
	MONTERREY_GALERIAS_PLAZA(202, "Monterrey - Galerias Plaza"),
	MORELIA_HOLIDAY_INN_EXPRESS(203, "Morelia - Holiday Inn Express Morelia"),
	MORELIA_FIESTA_INN_ALTOZANO(204, "Morelia - Fiesta Inn Morelia Altozano"),
	MORELIA_GAMMA_BELO(205, "Morelia - Gamma Morelia Belo"), MORELIA_HOLIDAY_INN(206, "Morelia - Holiday Inn Morelia"),
	MORELIA_FAIRFIELD_INN_SUITES(207, "Morelia - Fairfield Inn & Suites"),
	NOGALES_CITY_EXPRESS(208, "Nogales - City Express Nogales"),
	NOGALES_FIESTA_INN(209, "Nogales - Fiesta Inn Nogales"),
	NUEVO_LAREDO_REAL_INN(210, "Nuevo Laredo - Real Inn Nuevo Laredo"),
	NUEVO_LAREDO_CITY_EXPRESS(211, "Nuevo Laredo - City Express Nuevo Laredo"),
	NUEVO_LAREDO_HOLIDAY_INN_EXPRESS(212, "Nuevo Laredo - Holiday Inn Express Nuevo Laredo"),
	NUEVO_LAREDO_FIESTA_INN(213, "Nuevo Laredo - Fiesta Inn Nuevo Laredo"),
	NUEVO_VALLARTA_HAMPTON_BY_HILTON(214, "Nuevo Vallarta - Hampton by Hilton Nuevo Vallarta"),
	OAXACA_CITY_EXPRESS(215, "Oaxaca - City Express Oaxaca"), OAXACA_CITY_CENTRO(216, "Oaxaca - City Centro Oaxaca"),
	OAXACA_GRAND_FIESTA_AMERICANA(217, "Oaxaca - Grand Fiesta Americana Oaxaca"),
	OAXACA_HOLIDAY_INN_EXPRESS(218, "Oaxaca - Holiday Inn Express Oaxaca-centro Histórico"),
	OAXACA_FIESTA_INN(219, "Oaxaca - Fiesta Inn Oaxaca"),
	OAXACA_MISION_DE_LOS_ANGELES(220, "Oaxaca - Misión de los Ángeles"),
	ORIZABA_HOLIDAY_INN(221, "Orizaba - Holiday Inn Orizaba"),
	ORIZABA_GAMMA_DE_FRANCE(222, "Orizaba - Gamma Orizaba de France"),
	PACHUCA_FIESTA_INN_GRAN_PATIO(223, "Pachuca - Fiesta Inn Pachuca Gran Patio"),
	PACHUCA_HOLIDAY_INN_EXPRESS(224, "Pachuca - Holiday Inn Express Pachuca"),
	PACHUCA_CAMINO_REAL(225, "Pachuca - Camino Real Pachuca"),
	PARAISO_CITY_EXPRESS(226, "Paraíso - City Express Paraíso"),
	PIEDRAS_NEGRAS_CITY_EXPRESS(227, "Piedras Negras - City Express Piedras Negras"),
	PIEDRAS_NEGRAS_HOLIDAY_INN_EXPRESS(228, "Piedras Negras - Holiday Inn Express Piedras Negras"),
	PLAYA_DEL_CARMEN_GRAND_HYATT(229, "Playa del Camen - Grand Hyatt Playa del Carmen"),
	PLAYA_DEL_CARMEN_CITY_EXPRESS_SUITES(230, "Playa del Carmen - City Express Suites Playa del Carmen"),
	PLAYA_DEL_CARMEN_FIESTA_INN(231, "Playa del Carmen - Fiesta Inn Playa del Carmen"),
	POZA_RICA_FIESTA_INN(232, "Poza Rica, Ver - Fiesta Inn Poza Rica"),
	POZA_RICA_LA_QUINTA_WYNDHAM(233, "Poza Rica, Ver - La Quinta by Wyndham"),
	PUEBLA_FOUR_POINTS_BY_SHERATON(234, "Puebla - Four Points by Sheraton Puebla"),
	PUEBLA_CITY_EXPRESS_CENTRO(235, "Puebla - City Express Puebla Centro"),
	PUEBLA_GRAND_FIESTA_AMERICANA_ANGELOPOLIS(236, "Puebla - GRAND FIESTA AMERICANA ANGELOPOLIS"),
	PUEBLA_HILTON_GARDEN_INN_ANGELOPOLIS(237, "Puebla - Hilton Garden Inn Angelopolis"),
	PUEBLA_NH(238, "Puebla - NH Puebla"), PUEBLA_QUINTA_REAL(239, "Puebla - QUINTA REAL PUEBLA"),
	PUEBLA_COURTYARD_LAS_ANIMAS(240, "Puebla - Courtyard las Animas"),
	PUERTO_VALLARTA_HOLIDAY_INN_SUITES_MARINA(241,
			"Puerto Vallarta - Holiday Inn & Suites Puerto Vallarta Marina & Golf"),
	PUERTO_VALLARTA_KRYSTAL(242, "Puerto Vallarta - Krystal Puerto Vallarta"),
	PUERTO_VALLARTA_HOLIDAY_INN_EXPRESS(243, "Puerto Vallarta - Holiday Inn Express Puerto Vallarta"),
	PUERTO_VALLARTA_FIESTA_INN_LA_ISLA(244, "Puerto Vallarta - Fiesta Inn Puerto Vallarta La Isla"),
	QUERETARO_FIESTA_INN_EXPRESS_CONSTITUYENTES(245, "Querétaro - Fiesta Inn Express Querétaro Constituyentes"),
	QUERETARO_FIESTA_INN_CENTRO_SUR(246, "Querétaro - Fiesta Inn Queretaro Centro Sur"),
	QUERETARO_HOTSSON(247, "Querétaro - Hotsson Querétaro"), QUERETARO_NH(248, "Querétaro - NH Queretaro"),
	REYNOSA_CITY_EXPRESS(249, "Reynosa - City Express Reynosa"),
	REYNOSA_HOLIDAY_INN_ZONA_DORADA(250, "Reynosa - Holiday Inn Reynosa Zona Dorada"),
	REYNOSA_HAMPTON_INN_ZONA_INDUSTRIAL(251, "Reynosa - Hampton Inn Zona Industrial"),
	ROSARITO_CITY_EXPRESS(252, "Rosarito - City Express Rosarito"),
	SALAMANCA_CITY_EXPRESS(253, "Salamanca - City Express Salamanca"),
	SALAMANCA_HAMPTON_INN_SUITES_BAJIO(254, "Salamanca - Hampton Inn & Suites Salamanca Bajio"),
	SALAMANCA_BEL_AIR_BUSINESS_TRADEMARK(255, "Salamanca - Bel Air Business TradeMark"),
	SALINA_CRUZ_CITY_EXPRESS(256, "Salina Cruz - City Express Salina Cruz"),
	SALINA_CRUZ_ONE(257, "Salina Cruz - One Salina Cruz"),
	SALTILLO_CITY_EXPRESS_NORTE(258, "Saltillo - City Express Saltillo Norte"),
	SALTILLO_CITY_EXPRESS_SUR(259, "Saltillo - City Express Saltillo Sur"),
	SALTILLO_FIESTA_INN(260, "Saltillo - Fiesta Inn Saltillo"),
	SALTILLO_COURTYARD_BY_MARRIOTT(261, "Saltillo - Courtyard by Marriott Saltillo"),
	SAN_LUIS_POTOSI_REAL_INN(262, "San Luis Potosí - Real Inn San Luis Potosí"),
	SAN_LUIS_POTOSI_FIESTA_INN_GLORIETA_JUAREZ(263, "San Luis Potosí - Fiesta Inn Glorieta Juárez"),
	SAN_LUIS_POTOSI_FIESTA_AMERICANA(264, "San Luis Potosí - Fiesta Americana"),
	SAN_LUIS_POTOSI_COURTYARD(265, "San Luis Potosí - Courtyard San Luis Potosi"),
	SAN_LUIS_POTOSI_HYATT_REGENCY(266, "San Luis Potosí - Hyatt Regency San Luis Potosi"),
	SAN_MIGUEL_DE_ALLENDE_ALBOR_BY_HILTON(267, "San Miguel de Allende - Albor by Hilton"),
	SAN_MIGUEL_DE_ALLENDE_MISION(268, "San Miguel de Allende - Misión San Miguel de Allende"),
	SILAO_FAIRFIELD_INN_SUITES_AIRPORT(269, "Silao - Fairfield Inn & Suites Silao Guanajuato Airport"),
	SILAO_HILTON_GARDEN_INN(270, "Silao - Hilton Garden Inn Silao"),
	SILAO_FIESTA_INN_PUERTO_INTERIOR(271, "Silao - Fiesta Inn Silao Puerto Interior"),
	TAMPICO_CITY_EXPRESS_PLUS(272, "Tampico - City Express Plus Tampico"),
	TAMPICO_HAMPTON_INN_ZONA_DORADA(273, "Tampico - Hampton Inn by Hilton Tampico Zona Dorada"),
	TAMPICO_FIESTA_INN(274, "Tampico - Fiesta Inn Tampico"),
	TAMPICO_HOLIDAY_INN_ALTAMIRA(275, "Tampico - Holiday Inn Tampico Altamira"),
	TAMPICO_HOTSSON(276, "Tampico - Hotsson Tampico"), TAMPICO_GAMMA(277, "Tampico - Gamma"),
	TAPACHULA_HOLIDAY_INN_EXPRESS(278, "Tapachula - Holiday Inn Express Tapachula"),
	TAPACHULA_ONE(279, "Tapachula - One Tapachula"), TAPACHULA_CITY_EXPRESS(280, "Tapachula - City Express Tapachula"),
	TEPIC_CITY_EXPRESS(281, "Tepic - City Express Tepic"), TEPIC_FIESTA_INN(282, "Tepic - Fiesta Inn Tepic"),
	TIJUANA_MARRIOTT(283, "Tijuana - Marriott Tijuana Hotel"),
	TIJUANA_GRAND_HOTEL(284, "Tijuana - Grand Hotel Tijuana"), TIJUANA_HYATT_PLACE(285, "Tijuana - Hyatt Place"),
	TIJUANA_CITY_EXPRESS_RIO(286, "Tijuana - City Express Tijuana Río"),
	TIJUANA_REAL_INN(287, "Tijuana - Real Inn Tijuana"), TLAXCALA_HOLIDAY_INN(288, "Tlaxcala - Holiday Inn Tlaxcala"),
	TOLUCA_HOLIDAY_INN_EXPRESS_SUITES_AEROPUERTO(289,
			"Toluca - Holiday Inn Express & Suites Toluca zona Aeropuerto (Ahora es Holiday Inn Express Toluca Galerias Metepec)"),
	TOLUCA_FIESTA_INN_TOLLOCAN(290, "Toluca - Fiesta Inn Toluca Tollocan"),
	TOLUCA_DOUBLETREE_BY_HILTON(291, "Toluca - DoubleTree by Hilton Toluca"),
	TOLUCA_FIESTA_INN_CENTRO(292, "Toluca - Fiesta Inn Toluca Centro"),
	TORREON_REAL_INN(293, "Torreón - Real Inn Torreón"), TORREON_MARRIOTT(294, "Torreón - Marriott Torreón Hotel"),
	TORREON_FIESTA_INN_GALERIAS(295, "Torreón - Fiesta Inn Torreón Galerias"),
	TORREON_CITY_EXPRESS(296, "Torreón - City Express"),
	TUXPAN_HOLIDAY_INN_CONVENTION_CENTER(297, "Tuxpan - Holiday Inn Tuxpan - Convention Center"),
	TUXPAN_HOLIDAY_INN_EXPRESS(298, "Tuxpan - Holiday Inn Express Tuxpan"),
	TUXTLA_GUTIERREZ_HOLIDAY_INN_EXPRESS_MARIMBA(299,
			"Tuxtla Gutiérrez - Holiday Inn Express Tuxtla Gutierrez La Marimba"),
	TUXTLA_GUTIERREZ_MARRIOTT(300, "Tuxtla Gutiérrez - Marriott Tuxtla Gutierrez Hotel"),
	TUXTLA_GUTIERREZ_FIESTA_INN(301, "Tuxtla Gutiérrez - Fiesta Inn Tuxtla Gutierrez"),
	TUXTLA_GUTIERREZ_FIESTA_INN_FASHION_MALL(302, "Tuxtla Gutiérrez - Fiesta Inn Tuxtla Fashion Mall"),
	TUXTLA_GUTIERREZ_HOLIDAY_INN(303, "Tuxtla Gutiérrez - Holiday Inn Tuxtla"),
	URUAPAN_HOLIDAY_INN(304, "Uruapan - Holiday Inn Uruapan"),
	VERACRUZ_CAMINO_REAL_BOCA_DEL_RIO(305, "Veracruz - Camino Real Boca del Rio"),
	VERACRUZ_FIESTA_INN_MALECON(306, "Veracruz - Fiesta Inn Veracruz Malecon"),
	VERACRUZ_EMPORIO(307, "Veracruz - Emporio Veracruz"),
	VERACRUZ_GRAND_FIESTA_AMERICANA(308, "Veracruz - GRAND FIESTA AMERICANA"),
	VILLAHERMOSA_HYATT_REGENCY(309, "Villahermosa - Hyatt Regency Villahermosa"),
	VILLAHERMOSA_FIESTA_INN_CENCALLI(310, "Villahermosa - Fiesta Inn Villahermosa Cencalli"),
	VILLAHERMOSA_HOLIDAY_INN_EXPRESS_TABASCO_2000(311, "Villahermosa - Holiday Inn Express Villahermosa Tabasco 2000"),
	VILLAHERMOSA_MARRIOTT(312, "Villahermosa - Marriott Villahermosa Hotel"),
	XALAPA_CITY_EXPRESS(313, "Xalapa - City Express Xalapa"), XALAPA_FIESTA_INN(314, "Xalapa - Fiesta Inn Xalapa"),
	ZACATECAS_FIESTA_INN(315, "Zacatecas - Fiesta Inn Zacatecas"),
	ZACATECAS_EMPORIO(316, "Zacatecas - Emporio Zacatecas");

	private final int id;
	private final String nombre;

	DestinoViajeNacionalEnum(int id, String nombre) {
		this.id = id;
		this.nombre = nombre;
	}

	public int getId() {
		return id;
	}

	public String getNombre() {
		return nombre;
	}

	/** Búsqueda exacta por id */
	public static DestinoViajeNacionalEnum fromId(int id) {
		return Arrays.stream(values()).filter(e -> e.id == id).findFirst().orElse(null);
	}

	/**
	 * Búsqueda aproximada por descripción. - Ignora mayúsculas/minúsculas - Ignora
	 * acentos - Ignora signos de puntuación
	 */
	public static DestinoViajeNacionalEnum fromDescripcionAproximada(String descripcion) {
		if (descripcion == null || descripcion.isBlank()) {
			return null;
		}
		String normalizedInput = normalize(descripcion);
		String[] tokensInput = normalizedInput.split("\\s+");

		DestinoViajeNacionalEnum bestMatch = null;
		int bestScore = -1;

		for (DestinoViajeNacionalEnum e : values()) {
			String normalizedNombre = normalize(e.nombre);
			String[] tokensNombre = normalizedNombre.split("\\s+");

			int score = commonTokenCount(tokensInput, tokensNombre);

			if (score > bestScore) {
				bestScore = score;
				bestMatch = e;
			}
		}

		// Si el score es muy bajo, puedes retornar null
		return bestScore > 0 ? bestMatch : null;
	}

	private static int commonTokenCount(String[] a, String[] b) {
		int count = 0;
		for (String ta : a) {
			for (String tb : b) {
				if (!ta.isEmpty() && ta.equals(tb)) {
					count++;
					break;
				}
			}
		}
		return count;
	}

	private static String normalize(String s) {
		if (s == null)
			return "";
		String n = Normalizer.normalize(s, Normalizer.Form.NFD);
		n = n.replaceAll("\\p{M}", ""); // quita acentos
		n = n.replaceAll("[^\\p{Alnum}\\s]", ""); // quita signos, deja letras/números/espacios
		return n.toUpperCase(Locale.ROOT).trim();
	}
}
