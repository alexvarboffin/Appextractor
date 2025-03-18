//package com.walhalla.compose.utils
//
//import android.Manifest
//import com.walhalla.compose.model.PermissionCategory
//import com.walhalla.compose.model.SecurityLevel
//
//
//fun getPermissionCategory(permission: String): PermissionCategory {
//    return when {
//        permission.contains("CONTACTS") ||
//        permission.contains("CALENDAR") ||
//        permission.contains("ACCOUNTS") -> PermissionCategory.PERSONAL_DATA
//
//        permission.contains("LOCATION") -> PermissionCategory.LOCATION
//
//        permission.contains("STORAGE") ||
//        permission.contains("MEDIA") ||
//        permission.contains("FILES") -> PermissionCategory.STORAGE
//
//        permission.contains("CAMERA") ||
//        permission.contains("RECORD_AUDIO") ||
//        permission.contains("MICROPHONE") -> PermissionCategory.CAMERA_MIC
//
//        permission.contains("PHONE") ||
//        permission.contains("CALL") ||
//        permission.contains("SMS") -> PermissionCategory.PHONE
//
//        permission.contains("INTERNET") ||
//        permission.contains("NETWORK") -> PermissionCategory.NETWORK
//
//        permission.contains("BLUETOOTH") -> PermissionCategory.BLUETOOTH
//
//        permission.contains("NOTIFICATION") -> PermissionCategory.NOTIFICATIONS
//
//        permission.contains("SYSTEM") ||
//        permission.contains("DEVICE_ADMIN") ||
//        permission.contains("PACKAGE") -> PermissionCategory.SYSTEM
//
//        else -> PermissionCategory.OTHER
//    }
//}
//
//fun getPermissionDescription(permission: String): String {
//    return when (permission) {
//        Manifest.permission.READ_CONTACTS -> "Чтение контактов из телефонной книги"
//        Manifest.permission.WRITE_CONTACTS -> "Изменение контактов в телефонной книге"
//        Manifest.permission.READ_CALENDAR -> "Чтение событий календаря"
//        Manifest.permission.WRITE_CALENDAR -> "Создание и изменение событий календаря"
//        Manifest.permission.ACCESS_FINE_LOCATION -> "Точное определение местоположения (GPS)"
//        Manifest.permission.ACCESS_COARSE_LOCATION -> "Приблизительное определение местоположения (по сети)"
//        Manifest.permission.READ_EXTERNAL_STORAGE -> "Чтение файлов из хранилища"
//        Manifest.permission.WRITE_EXTERNAL_STORAGE -> "Запись файлов в хранилище"
//        Manifest.permission.CAMERA -> "Использование камеры"
//        Manifest.permission.RECORD_AUDIO -> "Запись звука через микрофон"
//        Manifest.permission.READ_PHONE_STATE -> "Получение информации о состоянии телефона"
//        Manifest.permission.CALL_PHONE -> "Совершение звонков"
//        Manifest.permission.SEND_SMS -> "Отправка SMS"
//        Manifest.permission.INTERNET -> "Доступ к интернету"
//        Manifest.permission.ACCESS_NETWORK_STATE -> "Получение информации о состоянии сети"
//        Manifest.permission.BLUETOOTH -> "Использование Bluetooth"
//        Manifest.permission.BLUETOOTH_ADMIN -> "Управление настройками Bluetooth"
//        Manifest.permission.POST_NOTIFICATIONS -> "Показ уведомлений"
//        else -> "Нет описания"
//    }
//}
//
//fun getSecurityRecommendation(category: PermissionCategory, protectionLevel: Int): String {
//    return when (category.securityLevel) {
//        SecurityLevel.HIGH -> "Предоставляйте это разрешение только если полностью доверяете приложению"
//        SecurityLevel.MEDIUM -> "Обычно безопасно, но следите за использованием"
//        SecurityLevel.LOW -> "Базовое разрешение, обычно безопасно"
//    }
//}