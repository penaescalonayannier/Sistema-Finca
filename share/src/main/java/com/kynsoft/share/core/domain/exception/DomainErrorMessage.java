package com.kynsoft.share.core.domain.exception;

import jakarta.annotation.Nullable;

public enum DomainErrorMessage implements IDomainErrorMessage {
    PATIENTS_NOT_FOUND(601, Series.DOMAIN_ERROR, "Pacientes no encontrados."),
    QUALIFICATION_NOT_FOUND(602, Series.DOMAIN_ERROR, "Calificación no encontrada."),
    QUALIFICATION_OR_ID_NULL(603, Series.DOMAIN_ERROR, "La calificación o el ID no pueden ser nulos."),
    BUSINESS_NOT_FOUND(604, Series.DOMAIN_ERROR, "Negocio no encontrado."),
    BUSINESS_OR_ID_NULL(605, Series.DOMAIN_ERROR, "El negocio o el ID no pueden ser nulos."),
    EXISTS_SCHEDULE_SOME_DATE_WHOSE_TIME_RANGE(606, Series.DOMAIN_ERROR, "Existe un horario en la misma fecha cuyo rango de tiempo coincide en algún momento con lo que deseas crear."),
    EXISTS_SCHEDULE_WITH_DATE_STARTTIME_ENDTIME(607, Series.DOMAIN_ERROR, "Existe un horario con la misma fecha, hora de inicio y hora de fin."),
    SCHEDULE_NOT_FOUND(608, Series.DOMAIN_ERROR, "Horario no encontrado."),
    SCHEDULE_CANNOT_BE_EQUALS_STARTTIME_ENDTIME(609, Series.DOMAIN_ERROR, "La hora de inicio y la hora de fin no pueden ser iguales."),
    SCHEDULE_DATE_LESS_THAN_CURRENT_DATE(610, Series.DOMAIN_ERROR, "La fecha proporcionada es menor que la fecha actual."),
    SCHEDULE_INITIAL_TIME_IS_PASSED(611, Series.DOMAIN_ERROR, "El tiempo inicial ha pasado."),
    SCHEDULE_END_TIME_IS_LESS_THAN(612, Series.DOMAIN_ERROR, "La hora de finalización proporcionada es menor que la hora de inicio."),
    SCHEDULE_EXISTS_SOME_TIME_STARTTIME_EDNTIME(613, Series.DOMAIN_ERROR, "Existe un horario con la misma fecha, hora de inicio y hora de fin."),
    RESOURCE_NOT_FOUND(614, Series.DOMAIN_ERROR, "Recurso no encontrado."),
    RECEIPT_NOT_FOUND(615, Series.DOMAIN_ERROR, "Recibo no encontrado."),
    STATUS_NOT_ACCEPTED(616, Series.DOMAIN_ERROR, "Estado no aceptado, la cita fue atendida."),
    SCHEDULE_IS_NOT_AVAIBLE(617, Series.DOMAIN_ERROR, "El horario seleccionado no está disponible."),
    COLUMN_UNIQUE(618, Series.DOMAIN_ERROR, "El valor de la clave duplicada viola la restricción única."),
    QUALIFICATION_DESCRIPTION_NOT_NULL(619, Series.DOMAIN_ERROR, "¡La descripción de la calificación no puede ser nula!"),
    QUALIFICATION_DESCRIPTION_UNIQUE(620, Series.DOMAIN_ERROR, "¡La descripción de la calificación debe ser única!"),
    PERMISSION_NOT_FOUND(621, Series.DOMAIN_ERROR, "Permiso no encontrado."),
    PERMISSION_OR_ID_NULL(622, Series.DOMAIN_ERROR, "El permiso o el ID no pueden ser nulos."),
    ROLE_PERMISSION_NOT_FOUND(623, Series.DOMAIN_ERROR, "Permiso de rol no encontrado."),
    RELATIONSHIP_MUST_BE_UNIQUE(624, Series.DOMAIN_ERROR, "La relación ya existe."),
    OBJECT_NOT_NULL(625, Series.DOMAIN_ERROR, "El objeto no puede ser nulo."),
    USER_ROLE_BUSINESS_NOT_FOUND(626, Series.DOMAIN_ERROR, "Rol de usuario-negocio no encontrado."),
    ROLE_NOT_FOUND(627, Series.DOMAIN_ERROR, "Rol no encontrado."),
    ROLE_EXIT(628, Series.DOMAIN_ERROR, "Rol no encontrado."),
    BUSINESS_MODULE_NOT_FOUND(629, Series.DOMAIN_ERROR, "Módulo de negocio no encontrado."),
    MODULE_PERMISSION_NOT_FOUND(630, Series.DOMAIN_ERROR, "Permiso del módulo no encontrado."),
    BUSINESS_RUC(631, Series.DOMAIN_ERROR, "El RUC del negocio debe tener trece caracteres."),
    BUSINESS_RUC_MUST_BY_UNIQUE(632, Series.DOMAIN_ERROR, "El RUC del negocio debe ser único."),
    BUSINESS_NAME_MUST_BY_UNIQUE(633, Series.DOMAIN_ERROR, "El nombre del negocio debe ser único."),
    SCHEDULED_TASK_ALREADY_EXISTS(634, Series.DOMAIN_ERROR, "Ya existe una tarea programada para este servicio."),
    SCHEDULE_TIME_CONFLICT(671, Series.DOMAIN_ERROR, "Existe un conflicto de horario. El recurso ya tiene un schedule asignado en el horario especificado."),
    MODULE_NAME_CANNOT_BE_EMPTY(635, Series.DOMAIN_ERROR, "El nombre del módulo no puede estar vacío."),
    MODULE_DESCRIPTION_CANNOT_BE_EMPTY(636, Series.DOMAIN_ERROR, "La descripción del módulo no puede estar vacía."),
    MODULE_NAME_MUST_BY_UNIQUE(637, Series.DOMAIN_ERROR, "El nombre del módulo debe ser único."),
    MODULE_NOT_FOUND(638, Series.DOMAIN_ERROR, "Módulo no encontrado."),
    GEOGRAPHIC_LOCATION_NOT_FOUND(639, Series.DOMAIN_ERROR, "Ubicación geográfica no encontrada."),
    USER_NOT_FOUND(640, Series.DOMAIN_ERROR, "Usuario no encontrado."),
    USER_PERMISSION_BUSINESS_NOT_FOUND(641, Series.DOMAIN_ERROR, "Permiso de usuario-negocio no encontrado."),
    PERMISSION_CODE_MUST_BY_UNIQUE(642, Series.DOMAIN_ERROR, "El código del permiso debe ser único."),
    PERMISSION_CODE_CANNOT_BE_EMPTY(643, Series.DOMAIN_ERROR, "El código del permiso no puede estar vacío."),
    DEVICE_NOT_FOUND(644, Series.DOMAIN_ERROR, "Dispositivo no encontrado."),
    DEVICE_IP_VALIDATE(645, Series.DOMAIN_ERROR, "La dirección IP no es correcta."),
    DEVICE_SERIAL_CANNOT_BE_EMPTY(646, Series.DOMAIN_ERROR, "El serial del dispositivo no puede estar vacío."),
    DEVICE_EMAIL_VALIDATE(647, Series.DOMAIN_ERROR, "La dirección de correo es incorrecta."),
    CUSTOMER_NOT_FOUND(648, Series.DOMAIN_ERROR, "Cliente no encontrado."),
    PATIENT_IDENTIFICATION_MUST_BY_UNIQUE(649, Series.DOMAIN_ERROR, "La identificación del paciente debe ser única."),
    SERVICE_TYPE_NAME_MUST_BY_UNIQUE(650, Series.DOMAIN_ERROR, "El nombre del tipo de servicio debe ser único."),
    SERVICE_NAME_MUST_BY_UNIQUE(651, Series.DOMAIN_ERROR, "El nombre del servicio debe ser único."),
    VACCINE_MUST_BY_UNIQUE(652, Series.DOMAIN_ERROR, "El nombre de la vacuna debe ser único."),
    PROCEDURE_NOT_FOUND(653, Series.DOMAIN_ERROR, "Procedimiento no encontrado."),
    PROCEDURE_CODE_MUST_BY_UNIQUE(654, Series.DOMAIN_ERROR, "El código del procedimiento debe ser único."),
    TREATMENT_NOT_FOUND(654, Series.DOMAIN_ERROR, "Tratamiento no encontrado."),
    DIAGNOSIS_NOT_FOUND(655, Series.DOMAIN_ERROR, "Diagnóstico no encontrado."),
    EXAM_NOT_FOUND(656, Series.DOMAIN_ERROR, "Examen no encontrado."),
    INSURANCE_NOT_FOUND(657, Series.DOMAIN_ERROR, "Seguro no encontrado."),
    SERVICE_TYPE_NOT_FOUND(658, Series.DOMAIN_ERROR, "Tipo de servicio no encontrado."),
    SERVICE_NOT_FOUND(659, Series.DOMAIN_ERROR, "Servicio no encontrado."),
    SCHEDULED_DATE_IS_NOT_PRESENT(660, Series.DOMAIN_ERROR, "La fecha debe estar presente."),
    CONTACT_INFO_NOT_FOUND(661, Series.DOMAIN_ERROR, "Información de contacto no encontrada."),
    MEDICAL_INFO_NOT_FOUND(662, Series.DOMAIN_ERROR, "Información médica no encontrada."),
    MEDICINES_NOT_FOUND(663, Series.DOMAIN_ERROR, "Medicinas no encontradas."),
    EXAM_ORDER_NOT_FOUND(664, Series.DOMAIN_ERROR, "Orden de examen no encontrada."),
    VACCINE_NOT_FOUND(665, Series.DOMAIN_ERROR, "Vacuna no encontrada."),
    PATIENT_VACCINE_NOT_FOUND(666, Series.DOMAIN_ERROR, "Relación no encontrada."),
    EXTERNAL_CONSULTATION_NOT_FOUND(667, Series.DOMAIN_ERROR, "Consulta externa no encontrada."),
    INTER_CONSULTATION_NOT_FOUND(668, Series.DOMAIN_ERROR, "Interconsulta no encontrada."),
    DOCTOR_NOT_FOUND(669, Series.DOMAIN_ERROR, "Doctor no encontrado."),
    CIE10_NOT_FOUND(670, Series.DOMAIN_ERROR, "CIE10 no encontrado."),
    NOT_DELETE(1000, Series.DOMAIN_ERROR, "El elemento no se puede eliminar porque tiene un elemento relacionado."),
    MUST_BY_UNIQUE(1002, Series.DOMAIN_ERROR, "Debe ser único."),
    OBJECT_NOT_FOUNT(1003, Series.DOMAIN_ERROR, "Objeto no encontrado."),
    UUID_NOT_FORMAT(1004, Series.DOMAIN_ERROR, "Formato de UUID incorrecto."),
    STATUS_NOT_FORMAT(1005, Series.DOMAIN_ERROR, "Estado no aceptado."),
    ITEM_ALREADY_EXITS(1006, Series.DOMAIN_ERROR, "Ya existe el elemento en el sistema."),
    DB_CONNECTION_NOT_FOUND(1007, Series.DOMAIN_ERROR, "Conexión no encontrada."),
    PAYMENT_NOT_FOUND(1009, Series.DOMAIN_ERROR, "No se pudo procesar el pago."),
    PASSWORD_MISMATCH(1008, Series.DOMAIN_ERROR, "La contraseña no coincide."),
    PLACE_CODE_MUST_BY_UNIQUE(1009, Series.DOMAIN_ERROR, "El código del lugar debe ser único."),
    PLACE_NAME_MUST_BY_UNIQUE(1010, Series.DOMAIN_ERROR, "El nombre del lugar debe ser único."),
    BLOCK_CODE_MUST_BY_UNIQUE(1011, Series.DOMAIN_ERROR, "El código del bloque debe ser único."),
    BLOCK_NAME_MUST_BY_UNIQUE(1012, Series.DOMAIN_ERROR, "El nombre del bloque debe ser único."),
    PROCEDURE_NAME_MUST_BY_UNIQUE(1013, Series.DOMAIN_ERROR, "El nombre del procedimiento debe ser único."),
    MEDICINES_NAME_MUST_BY_UNIQUE(1014, Series.DOMAIN_ERROR, "El nombre del medicamento debe ser único."),
    SERVICE_TYPE_CODE_MUST_BY_UNIQUE(1015, Series.DOMAIN_ERROR, "El código del tipo de servicio debe ser único."),
    SERVICE_CODE_MUST_BY_UNIQUE(1016, Series.DOMAIN_ERROR, "El código del servicio debe ser único."),
    CONSULT_EXTERN_DATE_EXP(1017, Series.DOMAIN_ERROR, "La consulta externa no se puede modificar. La fecha de creación ya pasó.."),
    BILLING_SERVICE_NOT_FOUND(659, Series.DOMAIN_ERROR, "Ya se encuentra registrado un pago con ese código para el paciente."),
    PAYMENT_NOT_PRESENT(2001, Series.DOMAIN_ERROR, "La información de pago no está disponible."),
    PAYMENT_NOT_REVERSE(2002, Series.DOMAIN_ERROR, "El pago solo puede ser reversado antes de las 11:59 PM del mismo día."),
    ACCOUNT_RECONCILIATION_NOT_FOUND(1018, Series.DOMAIN_ERROR, "Cuadre Contable no encontrado."),
    ALREADY_EXISTS(1019,Series.DOMAIN_ERROR , "Ya existe un registro con el mismo código."),
    ESPECIALITY_IS_NOT_ACCESS(1020, Series.DOMAIN_ERROR, "El especialista no tiene accesos al recurso solicitado."),
    PATIENT_ADMISSION_NOT_DELETE(1021, Series.DOMAIN_ERROR, "El paciente ya tiene una admisión hospitalaria en estado abierto. No se puede crear una nueva admisión hasta que la actual sea cerrada."),
    PATIENT_ADMISSION_ALREADY_EXISTS(1022, Series.DOMAIN_ERROR, "No se puede crear el registro porque ya existe una admisión hospitalaria activa para este paciente."),
    PATIENT_AGE_NOT_IN_VACCINE_RANGE(1023, Series.DOMAIN_ERROR, "La edad del paciente no está dentro del rango permitido para esta vacuna."),
    PATIENT_VACCINE_ALREADY_EXISTS(1024, Series.DOMAIN_ERROR, "El paciente ya tiene esta vacuna registrada."),
    LEAD_NOT_FOUND(1025, Series.DOMAIN_ERROR, "Lead no encontrado."),
    LEAD_EMAIL_ALREADY_EXISTS(1026, Series.DOMAIN_ERROR, "Ya existe un lead con este email."),

    // Two-Factor Authentication errors
    TWO_FACTOR_NOT_CONFIGURED(1030, Series.DOMAIN_ERROR, "2FA no configurado para este usuario."),
    TWO_FACTOR_ALREADY_ENABLED(1031, Series.DOMAIN_ERROR, "El usuario ya tiene 2FA activado."),
    TWO_FACTOR_NOT_ENABLED(1032, Series.DOMAIN_ERROR, "2FA no esta activado."),
    INVALID_TWO_FACTOR_CODE(1033, Series.DOMAIN_ERROR, "Codigo 2FA invalido."),
    TWO_FACTOR_LOCKED(1034, Series.DOMAIN_ERROR, "Cuenta bloqueada por demasiados intentos."),
    TWO_FACTOR_REQUIRED(1035, Series.DOMAIN_ERROR, "Se requiere verificacion 2FA."),
    QR_CODE_GENERATION_ERROR(1036, Series.DOMAIN_ERROR, "Error generando codigo QR."),

    // Billing and Subscription errors
    SUBSCRIPTION_NOT_FOUND(1040, Series.DOMAIN_ERROR, "Suscripción no encontrada."),
    SUBSCRIPTION_PENDING_EXISTS(1044, Series.DOMAIN_ERROR, "El cliente ya tiene una suscripción pendiente de tokenización."),
    BALANCE_NOT_FOUND(1041, Series.DOMAIN_ERROR, "Balance no encontrado."),
    INSUFFICIENT_BALANCE(1042, Series.DOMAIN_ERROR, "Balance insuficiente."),
    RATE_NOT_FOUND(1043, Series.DOMAIN_ERROR, "Tarifa de consumo no encontrada."),
    PAYMENT_PAYER_EMAIL_REQUIRED(1045, Series.DOMAIN_ERROR, "El email del pagador es obligatorio."),
    PAYMENT_PROVIDER_ERROR(1046, Series.DOMAIN_ERROR, "Error en el proveedor de pagos."),
    PAYMENT_PLAN_NOT_FOUND(1047, Series.DOMAIN_ERROR, "Plan de pago no encontrado."),
    PAYMENT_PLAN_CODE_MUST_BE_UNIQUE(1048, Series.DOMAIN_ERROR, "Ya existe un plan con este código para el merchant."),

    // Appointment/Receipt errors
    PATIENT_HAS_PENDING_APPOINTMENT(1050, Series.DOMAIN_ERROR, "El paciente ya tiene una cita pendiente para este servicio."),
    PATIENT_ALREADY_HAS_BOOKING_FOR_SCHEDULE(1049, Series.DOMAIN_ERROR, "El paciente ya tiene una reserva activa para este horario."),

    // Review errors
    REVIEW_NOT_FOUND(1051, Series.DOMAIN_ERROR, "Reseña no encontrada."),
    REVIEW_ALREADY_REPORTED(1052, Series.DOMAIN_ERROR, "La reseña ya ha sido reportada."),
    REVIEW_ALREADY_RESPONDED(1053, Series.DOMAIN_ERROR, "La reseña ya tiene una respuesta."),
    REVIEW_CANNOT_REPORT_OWN(1054, Series.DOMAIN_ERROR, "No puedes reportar tu propia reseña."),
    REVIEW_CANNOT_RESPOND_OWN(1055, Series.DOMAIN_ERROR, "No puedes responder a tu propia reseña."),
    REVIEW_CANNOT_UPDATE(1056, Series.DOMAIN_ERROR, "No puedes actualizar esta reseña."),
    REVIEW_INVALID_STATUS(1057, Series.DOMAIN_ERROR, "Estado de reseña inválido para esta operación."),
    REVIEW_ALREADY_ACTIVE(1058, Series.DOMAIN_ERROR, "La reseña ya está activa."),

    // UserTypePermission errors
    USER_TYPE_PERMISSION_NOT_FOUND(1060, Series.DOMAIN_ERROR, "Permiso por tipo de usuario no encontrado."),
    USER_TYPE_PERMISSION_ALREADY_EXISTS(1061, Series.DOMAIN_ERROR, "Ya existe este permiso para el tipo de usuario."),

    // Profile errors
    PROFILE_NOT_FOUND(1062, Series.DOMAIN_ERROR, "Perfil no encontrado."),
    PROFILE_NAME_MUST_BE_UNIQUE(1063, Series.DOMAIN_ERROR, "El nombre del perfil debe ser único."),

    // OTP errors
    OTP_EXPIRED(1070, Series.DOMAIN_ERROR, "El código ha expirado. Solicite uno nuevo."),
    OTP_INVALID(1071, Series.DOMAIN_ERROR, "El código ingresado es incorrecto."),

    // Appointment Package errors
    APPOINTMENT_PACKAGE_TEMPLATE_NOT_FOUND(1080, Series.DOMAIN_ERROR, "Plantilla de paquete de citas no encontrada."),
    APPOINTMENT_PACKAGE_NOT_FOUND(1081, Series.DOMAIN_ERROR, "Paquete de citas no encontrado."),
    APPOINTMENT_PACKAGE_QUOTA_NOT_FOUND(1082, Series.DOMAIN_ERROR, "Cuota de paquete no encontrada."),
    APPOINTMENT_PACKAGE_NO_QUOTA_AVAILABLE(1083, Series.DOMAIN_ERROR, "No hay cuota disponible para este servicio en el paquete."),
    APPOINTMENT_PACKAGE_EXPIRED(1084, Series.DOMAIN_ERROR, "El paquete de citas ha expirado."),
    APPOINTMENT_PACKAGE_NOT_ACTIVE(1085, Series.DOMAIN_ERROR, "El paquete de citas no está activo."),
    APPOINTMENT_PACKAGE_RESCHEDULE_NOT_ALLOWED(1086, Series.DOMAIN_ERROR, "Este paquete no permite reprogramar citas."),
    APPOINTMENT_PACKAGE_RESCHEDULE_LIMIT_REACHED(1087, Series.DOMAIN_ERROR, "Ha alcanzado el límite de reprogramaciones permitidas."),
    APPOINTMENT_PACKAGE_RESOURCE_CHANGE_NOT_ALLOWED(1088, Series.DOMAIN_ERROR, "Este paquete no permite cambiar de profesional."),
    APPOINTMENT_PACKAGE_RESOURCE_CHANGE_LIMIT_REACHED(1089, Series.DOMAIN_ERROR, "Ha alcanzado el límite de cambios de profesional permitidos."),
    APPOINTMENT_PACKAGE_CANCELLATION_NOT_ALLOWED(1090, Series.DOMAIN_ERROR, "Este paquete no permite cancelar citas."),
    APPOINTMENT_PACKAGE_MIN_HOURS_NOT_MET(1091, Series.DOMAIN_ERROR, "No se cumple el tiempo mínimo de anticipación requerido."),

    // Payment Order errors
    PAYMENT_ORDER_PENDING_EXISTS(2003, Series.DOMAIN_ERROR, "Ya existe una orden de pago pendiente de aprobación para este pagador."),
    PAYMENT_LINK_GENERATION_FAILED(2004, Series.DOMAIN_ERROR, "Error al generar el link de pago."),

    // Diagnosis errors
    DIAGNOSIS_ICD_CODE_REQUIRED(1100, Series.DOMAIN_ERROR, "El código CIE-10 del diagnóstico es obligatorio."),
    DIAGNOSIS_DUPLICATE_ICD_CODE(1101, Series.DOMAIN_ERROR, "Ya existe un diagnóstico con este código CIE-10 en esta consulta."),
    DIAGNOSIS_ICD_CODE_INVALID_FORMAT(1102, Series.DOMAIN_ERROR, "El código CIE-10 no tiene un formato válido."),

    // Appointment status transition errors
    INVALID_APPOINTMENT_STATUS_TRANSITION(1103, Series.DOMAIN_ERROR, "Transición de estado de cita inválida."),

    // Cash Shift errors
    CASH_SHIFT_NOT_FOUND(1110, Series.DOMAIN_ERROR, "Turno de caja no encontrado."),
    CASH_SHIFT_ALREADY_OPEN(1111, Series.DOMAIN_ERROR, "Ya existe un turno de caja abierto para este usuario y negocio."),
    CASH_SHIFT_ALREADY_CLOSED(1112, Series.DOMAIN_ERROR, "El turno de caja ya está cerrado."),

    // Consulting Room errors
    CONSULTING_ROOM_NOT_FOUND(1120, Series.DOMAIN_ERROR, "Consultorio no encontrado."),
    CONSULTING_ROOM_NUMBER_MUST_BE_UNIQUE(1121, Series.DOMAIN_ERROR, "El número de consultorio debe ser único dentro del mismo negocio."),

    // Doctor Consulting Room Session errors
    NO_ACTIVE_SESSION_FOUND(1130, Series.DOMAIN_ERROR, "No se encontró una sesión activa para el médico en este negocio."),

    // Commercial module errors
    ASEGURADORA_NOT_FOUND(1200, Series.DOMAIN_ERROR, "Aseguradora not found."),
    CONVENIO_NOT_FOUND(1201, Series.DOMAIN_ERROR, "Convenio not found."),
    COVERAGE_PERCENT_OUT_OF_RANGE(1202, Series.DOMAIN_ERROR, "Coverage percent must be between 0 and 100."),
    PRODUCT_NOT_FOUND(1203, Series.DOMAIN_ERROR, "Product not found."),
    PRICE_LIST_NOT_FOUND(1204, Series.DOMAIN_ERROR, "PriceList not found."),
    PRICE_LIST_ITEM_NOT_FOUND(1205, Series.DOMAIN_ERROR, "PriceListItem not found."),
    PRICE_LIST_ITEM_DUPLICATE(1206, Series.DOMAIN_ERROR, "PriceListItem already exists for this (priceList, product) pair."),
    ASEGURADORA_ALREADY_HAS_PRICE_LIST(1208, Series.DOMAIN_ERROR, "Aseguradora already has a PriceList. Only one PriceList per Aseguradora is allowed."),
    TARIFF_PRODUCT_PRICE_NOT_RESOLVABLE(1210, Series.DOMAIN_ERROR, "Product price cannot be resolved: no price defined for the requested PVP level or mainPrice fallback."),
    ASEGURADORA_WITHOUT_PRICE_LIST(1211, Series.DOMAIN_ERROR, "Aseguradora does not have an associated PriceList."),
    STOCK_LOCATION_NOT_FOUND(1212, Series.DOMAIN_ERROR, "Stock location not found."),
    PAYMENT_METHOD_NOT_FOUND(1213, Series.DOMAIN_ERROR, "Payment method not found."),

    // Accounts module errors
    ACCOUNT_NOT_FOUND(1300, Series.DOMAIN_ERROR, "Account not found."),
    ACCOUNT_ITEM_NOT_FOUND(1301, Series.DOMAIN_ERROR, "AccountItem not found."),
    ACCOUNT_INSURANCE_NOT_FOUND(1302, Series.DOMAIN_ERROR, "AccountInsurance not found."),
    ACCOUNT_STATUS_INVALID_TRANSITION(1303, Series.DOMAIN_ERROR, "Invalid account status transition."),
    ACCOUNT_CHARGE_REJECTED_NOT_OPEN(1304, Series.DOMAIN_ERROR, "Charges are only accepted on accounts in OPEN status."),
    INSURANCE_CLAIM_NOT_FOUND(1305, Series.DOMAIN_ERROR, "Insurance claim not found.");

    private static final DomainErrorMessage[] VALUES;

    static {
        VALUES = values();
    }

    private final int value;

    private final Series series;

    private final String reasonPhrase;

    DomainErrorMessage(int value, Series series, String reasonPhrase) {
        this.value = value;
        this.series = series;
        this.reasonPhrase = reasonPhrase;
    }

    @Override
    public int value() {
        return this.value;
    }

    /**
     * Return the status series of this status code.
     */
    public Series series() {
        return this.series;
    }

    /**
     * Return the reason phrase of this status code.
     */
    public String getReasonPhrase() {
        return this.reasonPhrase;
    }

    /**
     * Return a string representation of this status code.
     */
    @Override
    public String toString() {
        return this.value + " " + name();
    }

    /**
     * Return the {@code ApiErrorStatus} enum constant with the specified
     * numeric value.
     *
     * @param statusCode the numeric value of the enum to be returned
     * @return the enum constant with the specified numeric value
     * @throws IllegalArgumentException if this enum has no constant for the
     * specified numeric value
     */
    public static DomainErrorMessage valueOf(int statusCode) {
        DomainErrorMessage status = resolve(statusCode);
        if (status == null) {
            throw new IllegalArgumentException("No matching constant for [" + statusCode + "]");
        }
        return status;
    }

    /**
     * Resolve the given status code to an {@code ApiErrorStatus}, if possible.
     *
     * @param statusCode the ApiError status code (potentially non-standard)
     * @return the corresponding {@code ApiErrorStatus}, or {@code null} if not
     * found
     */
    @Nullable
    public static DomainErrorMessage resolve(int statusCode) {
        // Use cached VALUES instead of values() to prevent array allocation.
        for (DomainErrorMessage status : VALUES) {
            if (status.value == statusCode) {
                return status;
            }
        }
        return null;
    }

    /**
     * Enumeration of ApiError status series.
     * <p>
     * Retrievable via {@link DomainErrorMessage#series()}.
     */
    public enum Series {
        DOMAIN_ERROR
    }

}
