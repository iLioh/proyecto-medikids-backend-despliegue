package com.medikids.medikids.process.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${frontend.url:http://localhost:5173}")
    private String frontendUrl;

    @Value("${spring.mail.username}")
    private String mailFrom;

    /**
     * Envía un enlace de recuperación de contraseña.
     */
    @Async
    public void enviarEnlaceRecuperacion(String destinatario, String token) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(mailFrom);
            helper.setTo(destinatario);
            helper.setSubject("🔑 Medikids - Recuperación de Contraseña");
            String enlace = frontendUrl + "/restablecer-clave/" + token;
            helper.setText(buildRecoveryTemplate(enlace), true);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Error al enviar el correo de recuperación: " + e.getMessage(), e);
        }
    }

    /**
     * Envía un correo electrónico con el código de verificación 2FA.
     * El correo tiene un diseño HTML profesional con el branding de Medikids.
     *
     * @param destinatario Email del usuario destino
     * @param codigo       Código de 6 dígitos para verificación
     */
    public void enviarCodigo2FA(String destinatario, String codigo) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(mailFrom);
            helper.setTo(destinatario);
            helper.setSubject("🔐 Medikids - Código de Verificación");
            helper.setText(buildHtmlTemplate(codigo), true);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Error al enviar el correo de verificación: " + e.getMessage(), e);
        }
    }

    /**
     * Envía un correo de confirmación al registrar una cita,
     * con el comprobante de pago (boleta/factura) adjunto como PDF.
     *
     * @param destinatario    Email del cliente/responsable
     * @param nombrePaciente  Nombre completo del paciente (hijo)
     * @param nombreMedico    Nombre completo del médico asignado
     * @param especialidad    Especialidad del médico
     * @param fechaCita       Fecha de la cita (ej. "2026-06-15")
     * @param horaCita        Hora de la cita (ej. "10:30")
     * @param motivo          Motivo de la consulta
     * @param pdfBytes        Bytes del PDF del comprobante de pago (puede ser null)
     * @param tipoComprobante "boleta" o "factura" (para nombrar el adjunto)
     */
    @Async
    public void enviarConfirmacionCita(String destinatario, String nombrePaciente, String nombreMedico,
                                       String especialidad, String fechaCita, String horaCita,
                                       String motivo, byte[] pdfBytes, String tipoComprobante) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(mailFrom);
            helper.setTo(destinatario);
            helper.setSubject("✅ Medikids - Cita Registrada Exitosamente");
            helper.setText(buildCitaConfirmacionTemplate(
                    nombrePaciente, nombreMedico, especialidad, fechaCita, horaCita, motivo, tipoComprobante), true);

            // Adjuntar PDF comprobante si está disponible
            if (pdfBytes != null && pdfBytes.length > 0) {
                String nombreArchivo = "boleta".equalsIgnoreCase(tipoComprobante)
                        ? "Boleta_Medikids.pdf"
                        : "Factura_Medikids.pdf";
                helper.addAttachment(nombreArchivo,
                        new org.springframework.core.io.ByteArrayResource(pdfBytes),
                        "application/pdf");
            }

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Error al enviar el correo de confirmación de cita: " + e.getMessage(), e);
        }
    }

    /**
     * Construye el template HTML del correo de confirmación de cita.
     */
    private String buildCitaConfirmacionTemplate(String nombrePaciente, String nombreMedico,
                                                  String especialidad, String fechaCita,
                                                  String horaCita, String motivo, String tipoComprobante) {
        boolean esEfectivo = "efectivo".equalsIgnoreCase(tipoComprobante);
        String notaEfectivo = esEfectivo
                ? """
                  <table role="presentation" width="100%%" cellspacing="0" cellpadding="0"
                         style="background-color:#fffbeb; border:2px solid #f59e0b; border-radius:12px; margin:0 0 20px;">
                      <tr>
                          <td style="padding:16px 20px;">
                              <p style="color:#92400e; font-size:13px; font-weight:700; margin:0 0 4px;">💵 Pago pendiente en caja</p>
                              <p style="color:#78350f; font-size:13px; margin:0; line-height:1.6;">
                                  Seleccionaste <strong>pago en efectivo</strong>. Por favor, acércate a
                                  <strong>Caja Principal (Piso 1)</strong> con <strong>15 minutos de anticipación</strong>.
                                  Tu boleta o factura será emitida al momento del pago.
                              </p>
                          </td>
                      </tr>
                  </table>
                  """
                : "";
        return """
                <!DOCTYPE html>
                <html lang="es">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                </head>
                <body style="margin:0; padding:0; background-color:#f7f9f2; font-family:'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;">
                    <table role="presentation" width="100%%" cellspacing="0" cellpadding="0" style="background-color:#f7f9f2; padding:40px 0;">
                        <tr>
                            <td align="center">
                                <table role="presentation" width="480" cellspacing="0" cellpadding="0"
                                       style="background-color:#ffffff; border-radius:16px; box-shadow:0 4px 24px rgba(74,85,41,0.10); overflow:hidden;">

                                    <!-- Header -->
                                    <tr>
                                        <td style="background: linear-gradient(135deg, #9cb151 0%%, #5e6d31 100%%); padding:36px 40px; text-align:center;">
                                            <h1 style="color:#ffffff; margin:0; font-size:28px; font-weight:700; letter-spacing:1px;">
                                                Medikids
                                            </h1>
                                            <p style="color:rgba(255,255,255,0.90); margin:10px 0 0; font-size:14px; letter-spacing:0.5px;">
                                                Confirmación de Cita Médica
                                            </p>
                                        </td>
                                    </tr>

                                    <!-- Decorative Accent Line -->
                                    <tr>
                                        <td style="height:4px; background: linear-gradient(90deg, #b8ca76 0%%, #9cb151 50%%, #7c8f3e 100%%);"></td>
                                    </tr>

                                    <!-- Body -->
                                    <tr>
                                        <td style="padding:40px;">
                                            <p style="color:#4a5529; font-size:16px; margin:0 0 8px; font-weight:600;">
                                                ¡Cita registrada con éxito! ✅
                                            </p>
                                            <p style="color:#5e6d31; font-size:14px; line-height:1.7; margin:0 0 28px;">
                                                Te confirmamos que la siguiente cita ha sido agendada correctamente en el sistema Medikids.
                                            </p>

                                            <!-- Detalles de la cita -->
                                            <table role="presentation" width="100%%" cellspacing="0" cellpadding="0"
                                                   style="background: linear-gradient(135deg, #f7f9f2 0%%, #ecefdf 100%%); border:2px solid #b8ca76; border-radius:12px; overflow:hidden; margin:0 0 28px;">
                                                <tr>
                                                    <td style="padding:18px 24px; border-bottom:1px solid #dae1c0;">
                                                        <p style="color:#7c8f3e; font-size:11px; text-transform:uppercase; letter-spacing:1.5px; margin:0 0 4px; font-weight:600;">👶 Paciente</p>
                                                        <p style="color:#4a5529; font-size:15px; font-weight:700; margin:0;">%s</p>
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <td style="padding:18px 24px; border-bottom:1px solid #dae1c0;">
                                                        <p style="color:#7c8f3e; font-size:11px; text-transform:uppercase; letter-spacing:1.5px; margin:0 0 4px; font-weight:600;">👨‍⚕️ Médico</p>
                                                        <p style="color:#4a5529; font-size:15px; font-weight:700; margin:0;">%s</p>
                                                        <p style="color:#5e6d31; font-size:13px; margin:3px 0 0;">%s</p>
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <td style="padding:18px 24px; border-bottom:1px solid #dae1c0;">
                                                        <p style="color:#7c8f3e; font-size:11px; text-transform:uppercase; letter-spacing:1.5px; margin:0 0 4px; font-weight:600;">📅 Fecha</p>
                                                        <p style="color:#4a5529; font-size:15px; font-weight:700; margin:0;">%s</p>
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <td style="padding:18px 24px; border-bottom:1px solid #dae1c0;">
                                                        <p style="color:#7c8f3e; font-size:11px; text-transform:uppercase; letter-spacing:1.5px; margin:0 0 4px; font-weight:600;">🕐 Hora</p>
                                                        <p style="color:#4a5529; font-size:15px; font-weight:700; margin:0;">%s</p>
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <td style="padding:18px 24px;">
                                                        <p style="color:#7c8f3e; font-size:11px; text-transform:uppercase; letter-spacing:1.5px; margin:0 0 4px; font-weight:600;">📋 Motivo</p>
                                                        <p style="color:#4a5529; font-size:14px; margin:0;">%s</p>
                                                    </td>
                                                </tr>
                                            </table>

                                            %s

                                            <p style="color:#5e6d31; font-size:13px; line-height:1.6; margin:0 0 10px;">
                                                🏥 Por favor, preséntate con el menor <strong style="color:#4a5529;">10 minutos antes</strong> de la hora indicada.
                                            </p>
                                            <p style="color:#5e6d31; font-size:13px; line-height:1.6; margin:0;">
                                                Si necesitas cancelar o reprogramar la cita, contáctanos con anticipación.
                                            </p>
                                        </td>
                                    </tr>

                                    <!-- Footer -->
                                    <tr>
                                        <td style="background-color:#ecefdf; padding:20px 40px; border-top:1px solid #dae1c0; text-align:center;">
                                            <p style="color:#7c8f3e; font-size:12px; margin:0;">
                                                © 2026 Medikids · Todos los derechos reservados
                                            </p>
                                        </td>
                                    </tr>
                                </table>
                            </td>
                        </tr>
                    </table>
                </body>
                </html>
                """.formatted(nombrePaciente, nombreMedico, especialidad, fechaCita, horaCita, motivo, notaEfectivo);
    }

    /**
     * Construye el template HTML del correo con diseño profesional.
     */
    private String buildHtmlTemplate(String codigo) {
        return """
                <!DOCTYPE html>
                <html lang="es">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                </head>
                <body style="margin:0; padding:0; background-color:#f7f9f2; font-family:'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;">
                    <table role="presentation" width="100%%" cellspacing="0" cellpadding="0" style="background-color:#f7f9f2; padding:40px 0;">
                        <tr>
                            <td align="center">
                                <table role="presentation" width="480" cellspacing="0" cellpadding="0"
                                       style="background-color:#ffffff; border-radius:16px; box-shadow:0 4px 24px rgba(74,85,41,0.10); overflow:hidden;">

                                    <!-- Header -->
                                    <tr>
                                        <td style="background: linear-gradient(135deg, #9cb151 0%%, #5e6d31 100%%); padding:36px 40px; text-align:center;">
                                            <h1 style="color:#ffffff; margin:0; font-size:28px; font-weight:700; letter-spacing:1px;">
                                                Medikids
                                            </h1>
                                            <p style="color:rgba(255,255,255,0.90); margin:10px 0 0; font-size:14px; letter-spacing:0.5px;">
                                                Verificación de Seguridad
                                            </p>
                                        </td>
                                    </tr>

                                    <!-- Decorative Accent Line -->
                                    <tr>
                                        <td style="height:4px; background: linear-gradient(90deg, #b8ca76 0%%, #9cb151 50%%, #7c8f3e 100%%);"></td>
                                    </tr>

                                    <!-- Body -->
                                    <tr>
                                        <td style="padding:40px;">
                                            <p style="color:#4a5529; font-size:16px; margin:0 0 8px; font-weight:600;">
                                                ¡Hola! 👋
                                            </p>
                                            <p style="color:#5e6d31; font-size:14px; line-height:1.7; margin:0 0 28px;">
                                                Hemos recibido una solicitud de inicio de sesión en tu cuenta.
                                                Utiliza el siguiente código para completar la verificación:
                                            </p>

                                            <!-- Código -->
                                            <div style="background: linear-gradient(135deg, #f7f9f2 0%%, #ecefdf 100%%); border:2px dashed #b8ca76; border-radius:12px; padding:28px; text-align:center; margin:0 0 28px;">
                                                <p style="color:#7c8f3e; font-size:12px; text-transform:uppercase; letter-spacing:2px; margin:0 0 10px; font-weight:600;">
                                                    Tu código de acceso
                                                </p>
                                                <p style="color:#4a5529; font-size:38px; font-weight:800; letter-spacing:10px; margin:0; font-family:'Courier New', monospace;">
                                                    %s
                                                </p>
                                            </div>

                                            <p style="color:#5e6d31; font-size:13px; line-height:1.6; margin:0 0 10px;">
                                                ⏱️ Este código expirará en <strong style="color:#4a5529;">5 minutos</strong>.
                                            </p>
                                            <p style="color:#c0392b; font-size:13px; line-height:1.6; margin:0;">
                                                ⚠️ Si no solicitaste este código, ignora este correo. Tu cuenta está segura.
                                            </p>
                                        </td>
                                    </tr>

                                    <!-- Footer -->
                                    <tr>
                                        <td style="background-color:#ecefdf; padding:20px 40px; border-top:1px solid #dae1c0; text-align:center;">
                                            <p style="color:#7c8f3e; font-size:12px; margin:0;">
                                                © 2026 Medikids · Todos los derechos reservados
                                            </p>
                                        </td>
                                    </tr>
                                </table>
                            </td>
                        </tr>
                    </table>
                </body>
                </html>
                """.formatted(codigo);
    }

    /**
     * Construye el template HTML del correo de recuperación de contraseña.
     */
    private String buildRecoveryTemplate(String enlace) {
        return """
                <!DOCTYPE html>
                <html lang="es">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                </head>
                <body style="margin:0; padding:0; background-color:#f7f9f2; font-family:'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;">
                    <table role="presentation" width="100%%" cellspacing="0" cellpadding="0" style="background-color:#f7f9f2; padding:40px 0;">
                        <tr>
                            <td align="center">
                                <table role="presentation" width="480" cellspacing="0" cellpadding="0"
                                       style="background-color:#ffffff; border-radius:16px; box-shadow:0 4px 24px rgba(74,85,41,0.10); overflow:hidden;">

                                    <!-- Header -->
                                    <tr>
                                        <td style="background: linear-gradient(135deg, #9cb151 0%%, #5e6d31 100%%); padding:36px 40px; text-align:center;">
                                            <h1 style="color:#ffffff; margin:0; font-size:28px; font-weight:700; letter-spacing:1px;">
                                                Medikids
                                            </h1>
                                            <p style="color:rgba(255,255,255,0.90); margin:10px 0 0; font-size:14px; letter-spacing:0.5px;">
                                                Recuperación de Contraseña
                                            </p>
                                        </td>
                                    </tr>

                                    <!-- Decorative Accent Line -->
                                    <tr>
                                        <td style="height:4px; background: linear-gradient(90deg, #b8ca76 0%%, #9cb151 50%%, #7c8f3e 100%%);"></td>
                                    </tr>

                                    <!-- Body -->
                                    <tr>
                                        <td style="padding:40px;">
                                            <p style="color:#4a5529; font-size:16px; margin:0 0 8px; font-weight:600;">
                                                ¡Hola! 👋
                                            </p>
                                            <p style="color:#5e6d31; font-size:14px; line-height:1.7; margin:0 0 28px;">
                                                Hemos recibido una solicitud para restablecer la contraseña de tu cuenta.
                                                Haz clic en el botón de abajo para crear una nueva contraseña:
                                            </p>

                                            <!-- Botón de recuperación -->
                                            <div style="text-align:center; margin:0 0 28px;">
                                                <a href="%s" target="_blank"
                                                   style="display:inline-block; background:linear-gradient(135deg, #9cb151 0%%, #5e6d31 100%%); color:#ffffff; text-decoration:none; padding:16px 48px; border-radius:12px; font-size:16px; font-weight:700; letter-spacing:0.5px;">
                                                    Restablecer contraseña
                                                </a>
                                            </div>

                                            <p style="color:#5e6d31; font-size:13px; line-height:1.6; margin:0 0 10px;">
                                                O copia y pega este enlace en tu navegador:
                                            </p>
                                            <p style="color:#7c8f3e; font-size:12px; line-height:1.5; margin:0 0 20px; word-break:break-all; background:#f7f9f2; padding:10px 14px; border-radius:8px;">
                                                %s
                                            </p>

                                            <p style="color:#5e6d31; font-size:13px; line-height:1.6; margin:0 0 10px;">
                                                ⏱️ Este enlace expirará en <strong style="color:#4a5529;">15 minutos</strong>.
                                            </p>
                                            <p style="color:#c0392b; font-size:13px; line-height:1.6; margin:0;">
                                                ⚠️ Si no solicitaste este cambio, ignora este correo. Tu cuenta está segura.
                                            </p>
                                        </td>
                                    </tr>

                                    <!-- Footer -->
                                    <tr>
                                        <td style="background-color:#ecefdf; padding:20px 40px; border-top:1px solid #dae1c0; text-align:center;">
                                            <p style="color:#7c8f3e; font-size:12px; margin:0;">
                                                © 2026 Medikids · Todos los derechos reservados
                                            </p>
                                        </td>
                                    </tr>
                                </table>
                            </td>
                        </tr>
                    </table>
                </body>
                </html>
                """.formatted(enlace, enlace);
    }
}
