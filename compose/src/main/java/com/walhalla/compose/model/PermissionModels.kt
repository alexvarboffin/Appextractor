package com.walhalla.compose.model

enum class PermissionCategory(
    val title: String,
    val description: String,
    val securityLevel: SecurityLevel
) {
    PERSONAL_DATA("Личные данные", "Доступ к контактам, календарю и другим личным данным", SecurityLevel.HIGH),
    LOCATION("Местоположение", "Доступ к геолокации устройства", SecurityLevel.HIGH),
    STORAGE("Хранилище", "Доступ к файлам и медиа", SecurityLevel.MEDIUM),
    CAMERA_MIC("Камера и микрофон", "Доступ к камере и записи звука", SecurityLevel.HIGH),
    PHONE("Телефон", "Доступ к звонкам, SMS и состоянию телефона", SecurityLevel.HIGH),
    NETWORK("Сеть", "Доступ к интернету и сетевым функциям", SecurityLevel.MEDIUM),
    SYSTEM("Системные", "Системные разрешения и настройки", SecurityLevel.LOW),
    BLUETOOTH("Bluetooth", "Доступ к Bluetooth функциям", SecurityLevel.MEDIUM),
    NOTIFICATIONS("Уведомления", "Доступ к показу уведомлений", SecurityLevel.LOW),
    OTHER("Прочее", "Другие разрешения", SecurityLevel.MEDIUM);

}

enum class SecurityLevel(val title: String, val description: String) {
    HIGH("Высокий", "Требует особого внимания, может затрагивать конфиденциальные данные"),
    MEDIUM("Средний", "Стандартный уровень доступа к функциям устройства"),
    LOW("Низкий", "Базовый доступ к некритичным функциям")
}

data class PermissionInfo(
    val name: String,
    val simpleName: String,
    val description: String,
    val category: PermissionCategory,
    val protectionLevel: Int,
    val isGranted: Boolean,
    val recommendation: String
)

fun getPermissionCategory(permissionName: String): PermissionCategory {
    return when {
        permissionName.contains("CONTACTS") || 
        permissionName.contains("CALENDAR") || 
        permissionName.contains("ACCOUNTS") -> PermissionCategory.PERSONAL_DATA
        
        permissionName.contains("LOCATION") -> PermissionCategory.LOCATION
        
        permissionName.contains("STORAGE") || 
        permissionName.contains("MEDIA") || 
        permissionName.contains("FILES") -> PermissionCategory.STORAGE
        
        permissionName.contains("CAMERA") || 
        permissionName.contains("RECORD_AUDIO") || 
        permissionName.contains("MICROPHONE") -> PermissionCategory.CAMERA_MIC
        
        permissionName.contains("PHONE") || 
        permissionName.contains("CALL") || 
        permissionName.contains("SMS") -> PermissionCategory.PHONE
        
        permissionName.contains("INTERNET") || 
        permissionName.contains("NETWORK") -> PermissionCategory.NETWORK
        
        permissionName.contains("BLUETOOTH") -> PermissionCategory.BLUETOOTH
        
        permissionName.contains("NOTIFICATION") -> PermissionCategory.NOTIFICATIONS
        
        permissionName.contains("SYSTEM") || 
        permissionName.contains("PACKAGE") || 
        permissionName.contains("BOOT") -> PermissionCategory.SYSTEM
        
        else -> PermissionCategory.OTHER
    }
}

fun getPermissionDescription(permissionName: String): String {
    return when {
        permissionName.contains("READ_CONTACTS") -> 
            "Позволяет приложению читать данные контактов пользователя"
        permissionName.contains("WRITE_CONTACTS") -> 
            "Позволяет приложению изменять данные контактов пользователя"
        permissionName.contains("ACCESS_FINE_LOCATION") -> 
            "Предоставляет точный доступ к местоположению устройства через GPS"
        permissionName.contains("ACCESS_COARSE_LOCATION") -> 
            "Предоставляет приблизительный доступ к местоположению через сети"
        permissionName.contains("CAMERA") -> 
            "Разрешает доступ к камере устройства для съемки фото и видео"
        permissionName.contains("RECORD_AUDIO") -> 
            "Разрешает запись аудио через микрофон устройства"
        permissionName.contains("READ_EXTERNAL_STORAGE") -> 
            "Позволяет читать файлы из внешнего хранилища"
        permissionName.contains("WRITE_EXTERNAL_STORAGE") -> 
            "Позволяет записывать файлы во внешнее хранилище"
        permissionName.contains("INTERNET") -> 
            "Разрешает приложению доступ к интернету"
        permissionName.contains("BLUETOOTH") -> 
            "Предоставляет доступ к функциям Bluetooth"
        else -> "Предоставляет доступ к системным функциям устройства"
    }
}

fun getSecurityRecommendation(category: PermissionCategory, protectionLevel: Int): String {
    return when (category) {
        PermissionCategory.PERSONAL_DATA -> 
            "Предоставляйте это разрешение только доверенным приложениям. Данные могут быть использованы для идентификации."
        PermissionCategory.LOCATION -> 
            "Убедитесь, что приложению действительно необходим доступ к местоположению. Рассмотрите вариант с приблизительной локацией."
        PermissionCategory.CAMERA_MIC -> 
            "Предоставляйте доступ только когда используете соответствующие функции. Следите за индикаторами использования."
        PermissionCategory.STORAGE -> 
            "Убедитесь, что приложение запрашивает доступ только к необходимым типам файлов."
        PermissionCategory.PHONE -> 
            "Будьте осторожны - эти разрешения могут привести к дополнительным расходам или утечке данных."
        PermissionCategory.NETWORK -> 
            "Рекомендуется использовать защищенное соединение и следить за расходом трафика."
        PermissionCategory.BLUETOOTH -> 
            "Предоставляйте доступ только при необходимости использования Bluetooth функций."
        PermissionCategory.NOTIFICATIONS -> 
            "Безопасное разрешение, но может привести к нежелательным уведомлениям."
        PermissionCategory.SYSTEM -> 
            "Системные разрешения могут влиять на работу устройства. Предоставляйте с осторожностью."
        PermissionCategory.OTHER -> 
            "Внимательно изучите, для чего приложению нужно это разрешение."
    }
} 