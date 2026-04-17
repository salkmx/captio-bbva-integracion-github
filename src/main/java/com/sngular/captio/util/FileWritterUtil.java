/**
 * 
 */
package com.sngular.captio.util;

/**
 * Clase de utileria y métodos auxiliares para escribir cosas en los archivos
 */
public class FileWritterUtil {
	private static final String FORMAT_PERMISOS_USUARIOS =
	        "%-10s # %-14s # %-10s # %-20s%n";
	
	public static String getHeaderUsuarioPermisos() {
		StringBuilder sb = new StringBuilder();
        sb.append(String.format(FORMAT_PERMISOS_USUARIOS.replace("#", "|"),
                "Id usuario",
                "Código Usuario",
                "Id Permiso",
                "Descripción permiso"));
        sb.append(addNewEndLine());
        return sb.toString();
	}
	
	public static String addNewEndLine() {
		StringBuilder sb = new StringBuilder();
        sb.append(String.format(
        		FORMAT_PERMISOS_USUARIOS.replace("#", "+"),
                "----------",
                "--------------",
                "----------",
                "--------------------"
        ));
        return sb.toString();
	}
	
	public static String getLineUsuarioPermisos(String idUser, String userCode, int idPermission, String permissionDescription) {
		StringBuilder sb = new StringBuilder();
		sb.append(String.format(FORMAT_PERMISOS_USUARIOS.replace("#", "|"),
				idUser,
				userCode,
				idPermission,
				permissionDescription
        ));
        return sb.toString();
	}
}
