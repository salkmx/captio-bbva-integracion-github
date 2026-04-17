package com.sngular.captio.config;

import java.util.Map;
import java.util.Set;

import com.sngular.captio.enums.GrupoEnum;

/**
 * Configuración centralizada de equivalencias para asignación de grupos.
 * Permite gestionar los mapeos de puestos y direcciones de forma declarativa.
 */
public class GrupoEquivalenciasConfig {

        /**
         * Set de puestos CIB válidos (según columna oficial "LIMITE CARACTERES SONAR
         * 40").
         * Coincidencia exacta requerida para 0 falsos positivos.
         * Total: 79 puestos originales -> 65 únicos (algunos colisionan al truncar).
         * Fuente: Equivalencias puestos.xlsx - Ejecutivos CIB.csv
         */
        public static final Set<String> PUESTOS_CIB_VALIDOS = Set.of(
                        // Global Banker (13 únicos - ninguno truncado)
                        "global banker analyst i mx 4",
                        "global banker analyst ii mx 1",
                        "global banker associate lead mx 1",
                        "global banker associate mx 2",
                        "global banker executive director mx 2",
                        "global banker managing director mx 6",
                        "global banker managing director mx 7",
                        "global banker senior analyst mx 2",
                        "global banker senior associate mx 3",
                        "global banker senior vice president mx 4",
                        "global banker senior vice president mx 5",
                        "global banker vice president mx 4",
                        "global banker vice president mx 5",

                        // Sales Corporate (8 únicos)
                        "sales corporate balance sheet solutions ",
                        "sales corporate fx flow analyst ii mx 1",
                        "sales corporate fx flow associate lead m",
                        "sales corporate fx flow associate mx 1",
                        "sales corporate fx flow vice president m",
                        "sales corporate multiproduct sales analy",
                        "sales corporate multiproduct sales execu",
                        "sales corporate multiproduct sales senio",

                        // Sales GT&IB (1)
                        "sales gt&ib managing director mx 1",

                        // Sales Institutional (12 únicos)
                        "sales institutional equity flow cash ass",
                        "sales institutional equity flow cash exe",
                        "sales institutional ficc flow analyst ii",
                        "sales institutional ficc flow associate ",
                        "sales institutional ficc flow executive ",
                        "sales institutional ficc flow senior ass",
                        "sales institutional ficc flow senior vic",
                        "sales institutional ficc flow vice presi",
                        "sales institutional solutions analyst i ",
                        "sales institutional solutions executive ",
                        "sales institutional solutions senior ana",
                        "sales institutional solutions vice presi",

                        // Sales Managing Director (1 único - cubre MX 4 y MX 5)
                        "sales managing director - country/region",

                        // Sales Networks Flow Execution (3 únicos)
                        "sales networks flow execution analyst i ",
                        "sales networks flow execution executive ",
                        "sales networks flow execution vice presi",

                        // Sales Networks FX Flow (8 únicos)
                        "sales networks fx flow analyst i mx 4",
                        "sales networks fx flow associate lead mx",
                        "sales networks fx flow associate mx 4",
                        "sales networks fx flow associate mx 5",
                        "sales networks fx flow executive directo",
                        "sales networks fx flow senior analyst mx",
                        "sales networks fx flow senior associate ",
                        "sales networks fx flow vice president mx",

                        // Sales Networks Investment Products (4 únicos)
                        "sales networks investment products analy",
                        "sales networks investment products manag",
                        "sales networks investment products senio",
                        "sales networks investment products vice ",

                        // Sales Networks Multiproduct Sales (4 únicos)
                        "sales networks multiproduct sales associ",
                        "sales networks multiproduct sales execut",
                        "sales networks multiproduct sales senior",
                        "sales networks multiproduct sales vice p",

                        // Transaction Banker (11 únicos)
                        "transaction banker analyst i mx 4",
                        "transaction banker analyst ii mx 1",
                        "transaction banker associate lead mx 3",
                        "transaction banker associate mx 2",
                        "transaction banker executive director mx",
                        "transaction banker senior analyst mx 2",
                        "transaction banker senior associate mx 2",
                        "transaction banker senior vice president",
                        "transaction banker team leader executive",
                        "transaction banker team leader managing ",
                        "transaction banker vice president mx 3");

        /**
         * Set de puestos Cash Management válidos.
         * Coincidencia exacta requerida (truncado a 40 chars si aplica).
         * Fuente: equivalencias puestos - Ejemplos iniciales.
         */
        public static final Set<String> PUESTOS_CASH_MANAGEMENT_VALIDOS = Set.of(
                        "ejecutivo soluciones transaccionales ban",
                        "ejecutivo soluciones transaccionales be&");

        /**
         * Set de puestos BEyG válidos.
         * Coincidencia exacta requerida (truncado a 40 chars si aplica).
         * Fuente: equivalencias puestos - Ejemplos iniciales.
         */
        public static final Set<String> PUESTOS_EJECUTIVOS_BEYG_VALIDOS = Set.of(
                        "ejecutivo clientes institucionales & gob",
                        "ejecutivo comercial banca de empresas i");

        public static final Set<String> PERSONAL_DE_RETAIL_VALIDOS = Set.of(
                        "cs retail associate business execution i",
                        "retail manager solutions development i m",
                        "cs retail manager solutions development",
                        "retail principal manager solutions devel",
                        "cs retail principal manager solutions de",
                        "retail senior manager solutions developm",
                        "retail risk principal manager i",
                        "retail risk principal manager ii",
                        "retail risk principal manager ii mx 1",
                        "retail risk principal manager ii mx 2",
                        "retail risk principal manager ii mx 3",
                        "retail risk principal manager ii mx 4",
                        "retail risk principal manager ii mx 5",
                        "retail risk principal manager ii mx 6",
                        "retail risk manager i");

        /**
         * Set de direcciones Bca Patrimonial y Privada válidas (coincidencia exacta, 0
         * falsos positivos).
         * Valores normalizados. Usuario debe completar con direcciones reales.
         */
        public static final Set<String> DIRECCIONES_BCA_PATRIMONIAL_PRIVADA_VALIDAS = Set.of(
                        "director red patrimonial y privada",
                        "d. divisional pyp metro mx",
                        "d. divisional pyp mx 1");

        /**
         * Set de direcciones Divisional válidas (coincidencia exacta, 0 falsos
         * positivos).
         * Valores normalizados. Usuario debe completar con direcciones reales.
         */
        public static final Set<String> DIRECCIONES_DIVISIONAL_VALIDAS = Set.of(
                        "d. divisional",
                        "director divisional comercial mx 2",
                        "d. divisional ii mx",
                        "d. divisional pyme metro mx",
                        "d. divisional pyme mx",
                        "director divisional hipotecario mex");

        /**
         * Set de direcciones Regional válidas (coincidencia exacta, 0 falsos
         * positivos).
         * Valores normalizados. Usuario debe completar con direcciones reales.
         */
        public static final Set<String> DIRECCIONES_REGIONAL_VALIDAS = Set.of(
                        "director regional mx");

        /**
         * Set de direcciones Zona válidas (coincidencia exacta, 0 falsos positivos).
         * Valores normalizados. Usuario debe completar con direcciones reales.
         */
        public static final Set<String> DIRECCIONES_ZONA_VALIDAS = Set.of(
                        "director de zona i",
                        "director de zona ii",
                        "director de zona banca remota i");

        private GrupoEquivalenciasConfig() {
                // Clase de utilidad, no instanciable
        }
}
