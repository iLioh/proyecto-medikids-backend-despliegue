package com.medikids.medikids.process.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.EnumMap;
import java.util.Map;
import java.util.Random;

@Service
public class PdfService {

    // Paleta de colores Medikids
    private static final Color COLOR_VERDE_OSCURO = new Color(0x4a, 0x55, 0x29);
    private static final Color COLOR_VERDE_MEDIO  = new Color(0x9c, 0xb1, 0x51);
    private static final Color COLOR_VERDE_CLARO  = new Color(0xec, 0xef, 0xdf);
    private static final Color COLOR_GRIS_TEXTO   = new Color(0x55, 0x55, 0x55);
    private static final Color COLOR_BLANCO       = Color.WHITE;
    private static final Color COLOR_LETRAS_BG    = new Color(0xf0, 0xf5, 0xe0);

    /**
     * Genera un PDF comprobante de pago (boleta o factura) para una cita médica.
     *
     * @param tipoComprobante   "boleta" o "factura"
     * @param numeroDocumento   DNI (boleta) o RUC (factura)
     * @param nombreRazonSocial Nombre o razón social del receptor
     * @param nombrePaciente    Nombre del paciente
     * @param nombreMedico      Nombre completo del médico
     * @param especialidad      Especialidad médica
     * @param fechaCita         Fecha de la cita
     * @param horaCita          Hora de la cita
     * @param metodoPago        Método de pago utilizado
     * @return PDF en bytes listo para adjuntar al correo
     */
    public byte[] generarComprobante(String tipoComprobante, String numeroDocumento,
                                     String nombreRazonSocial, String nombrePaciente,
                                     String nombreMedico, String especialidad,
                                     String fechaCita, String horaCita, String metodoPago) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            Document document = new Document(PageSize.A4, 50, 50, 50, 50);
            PdfWriter writer = PdfWriter.getInstance(document, baos);
            document.open();

            boolean esFactura = "factura".equalsIgnoreCase(tipoComprobante);
            String tipoLabel      = esFactura ? "FACTURA ELECTRÓNICA" : "BOLETA DE VENTA ELECTRÓNICA";
            String serie          = esFactura ? "F001" : "B001";
            String correlativo    = String.format("%08d", new Random().nextInt(90000000) + 10000000);
            String nroComprobante = serie + "-" + correlativo;
            String fechaEmision   = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            String horaEmision    = java.time.LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));

            // Monto fijo (S/ 80.00) — subtotal + IGV
            double montoTotal  = 80.00;
            double igv         = Math.round(montoTotal / 1.18 * 0.18 * 100.0) / 100.0;
            double subtotal    = Math.round((montoTotal - igv) * 100.0) / 100.0;
            String montoEnLetras = numeroALetras(montoTotal);

            // ── ENCABEZADO (2 columnas: Empresa | Tipo+Número+Fecha+Hora) ────────────


            PdfPTable header = new PdfPTable(2);
            header.setWidthPercentage(100);
            header.setWidths(new float[]{55f, 45f});
            header.setSpacingAfter(20f);

            // Celda izquierda: logo + datos empresa
            PdfPCell celdaEmpresa = new PdfPCell();
            celdaEmpresa.setBorder(Rectangle.NO_BORDER);
            celdaEmpresa.setPadding(12f);
            celdaEmpresa.setBackgroundColor(COLOR_VERDE_OSCURO);

            Font fEmpresaNombre = new Font(Font.HELVETICA, 20f, Font.BOLD, COLOR_BLANCO);
            Font fEmpresaDato   = new Font(Font.HELVETICA, 8f, Font.NORMAL, new Color(0xcc, 0xdd, 0xaa));

            Paragraph pEmpresa = new Paragraph("Medikids", fEmpresaNombre);
            pEmpresa.setSpacingAfter(4f);
            celdaEmpresa.addElement(pEmpresa);
            celdaEmpresa.addElement(new Paragraph("RUC: 20123456789", fEmpresaDato));
            celdaEmpresa.addElement(new Paragraph("Los Antares 320. Of.809 Torre A - Surco", fEmpresaDato));
            celdaEmpresa.addElement(new Paragraph("970654221", fEmpresaDato));
            header.addCell(celdaEmpresa);

            // Celda derecha: tipo + número + fecha + hora de emisión
            PdfPCell celdaTipo = new PdfPCell();
            celdaTipo.setBorder(Rectangle.BOX);
            celdaTipo.setBorderColor(COLOR_VERDE_MEDIO);
            celdaTipo.setBorderWidth(2f);
            celdaTipo.setPadding(12f);
            celdaTipo.setHorizontalAlignment(Element.ALIGN_CENTER);
            celdaTipo.setVerticalAlignment(Element.ALIGN_MIDDLE);

            Font fTipoTitle = new Font(Font.HELVETICA, 11f, Font.BOLD, COLOR_VERDE_OSCURO);
            Font fTipoNum   = new Font(Font.HELVETICA, 13f, Font.BOLD, COLOR_VERDE_MEDIO);
            Font fTipoDato  = new Font(Font.HELVETICA, 8f, Font.NORMAL, COLOR_GRIS_TEXTO);

            Paragraph pTipo = new Paragraph(tipoLabel, fTipoTitle);
            pTipo.setAlignment(Element.ALIGN_CENTER);
            pTipo.setSpacingAfter(6f);
            celdaTipo.addElement(pTipo);

            Paragraph pNum = new Paragraph(nroComprobante, fTipoNum);
            pNum.setAlignment(Element.ALIGN_CENTER);
            pNum.setSpacingAfter(8f);
            celdaTipo.addElement(pNum);

            celdaTipo.addElement(centrado("Fecha de emisión:", fTipoDato));
            celdaTipo.addElement(centrado(fechaEmision + "  " + horaEmision,
                    new Font(Font.HELVETICA, 9f, Font.BOLD, COLOR_VERDE_OSCURO)));
            header.addCell(celdaTipo);
            document.add(header);

            // Generar QR (Opción B: datos del comprobante en texto)
            String qrContenido = (esFactura ? "FACTURA ELECTRÓNICA" : "BOLETA DE VENTA ELECTRÓNICA") + "\n"
                    + nroComprobante + "\n"
                    + (esFactura ? "RUC CLIENTE: " : "DNI CLIENTE: ") + numeroDocumento + "\n"
                    + "CLIENTE: " + nombreRazonSocial + "\n"
                    + "FECHA: " + fechaEmision + "  " + horaEmision + "\n"
                    + "MONTO: S/ " + String.format("%.2f", montoTotal);
            byte[] qrBytes = generarQrBytes(qrContenido, 120);

            // ── DATOS DEL RECEPTOR ────────────────────────────────────────
            Font fSeccionTitle = new Font(Font.HELVETICA, 9f, Font.BOLD, COLOR_BLANCO);
            Font fLabel        = new Font(Font.HELVETICA, 8f, Font.BOLD, COLOR_VERDE_OSCURO);
            Font fValor        = new Font(Font.HELVETICA, 9f, Font.NORMAL, COLOR_GRIS_TEXTO);

            PdfPTable tReceptor = new PdfPTable(1);
            tReceptor.setWidthPercentage(100);
            tReceptor.setSpacingAfter(0f);

            PdfPCell seccionReceptor = new PdfPCell(new Phrase(
                    "  DATOS DEL " + (esFactura ? "CLIENTE / EMPRESA" : "CLIENTE"), fSeccionTitle));
            seccionReceptor.setBackgroundColor(COLOR_VERDE_MEDIO);
            seccionReceptor.setBorder(Rectangle.NO_BORDER);
            seccionReceptor.setPadding(6f);
            tReceptor.addCell(seccionReceptor);
            document.add(tReceptor);

            PdfPTable tReceptorDatos = new PdfPTable(2);
            tReceptorDatos.setWidthPercentage(100);
            tReceptorDatos.setWidths(new float[]{30f, 70f});
            tReceptorDatos.setSpacingAfter(16f);
            agregarFila(tReceptorDatos, esFactura ? "RUC:" : "DNI:", numeroDocumento, fLabel, fValor);
            agregarFila(tReceptorDatos, esFactura ? "Razón Social:" : "Nombre:", nombreRazonSocial, fLabel, fValor);
            document.add(tReceptorDatos);

            // ── DETALLE DEL SERVICIO ──────────────────────────────────────
            PdfPTable tDetalleTitulo = new PdfPTable(1);
            tDetalleTitulo.setWidthPercentage(100);
            tDetalleTitulo.setSpacingBefore(8f);
            tDetalleTitulo.setSpacingAfter(0f);

            PdfPCell seccionDetalle = new PdfPCell(new Phrase("  DETALLE DEL SERVICIO", fSeccionTitle));
            seccionDetalle.setBackgroundColor(COLOR_VERDE_MEDIO);
            seccionDetalle.setBorder(Rectangle.NO_BORDER);
            seccionDetalle.setPadding(6f);
            tDetalleTitulo.addCell(seccionDetalle);
            document.add(tDetalleTitulo);

            PdfPTable tItems = new PdfPTable(new float[]{8f, 42f, 20f, 15f, 15f});
            tItems.setWidthPercentage(100);
            tItems.setSpacingAfter(10f);

            Font fColHead = new Font(Font.HELVETICA, 8f, Font.BOLD, COLOR_VERDE_OSCURO);
            agregarCeldaHeader(tItems, "CANT.", fColHead, COLOR_VERDE_CLARO);
            agregarCeldaHeader(tItems, "DESCRIPCIÓN", fColHead, COLOR_VERDE_CLARO);
            agregarCeldaHeader(tItems, "MÉDICO", fColHead, COLOR_VERDE_CLARO);
            agregarCeldaHeader(tItems, "FECHA", fColHead, COLOR_VERDE_CLARO);
            agregarCeldaHeader(tItems, "PRECIO", fColHead, COLOR_VERDE_CLARO);

            Font fItemVal = new Font(Font.HELVETICA, 8.5f, Font.NORMAL, COLOR_GRIS_TEXTO);
            Font fItemNeg = new Font(Font.HELVETICA, 8.5f, Font.BOLD, COLOR_VERDE_OSCURO);
            String descripcion = "Consulta Médica - " + especialidad;
            String fechaHora   = (fechaCita != null ? fechaCita : "—") + "\n" + (horaCita != null ? horaCita : "");

            agregarCeldaItem(tItems, "1", fItemVal, Element.ALIGN_CENTER);
            agregarCeldaItem(tItems, descripcion, fItemVal, Element.ALIGN_LEFT);
            agregarCeldaItem(tItems, "Dr(a). " + nombreMedico, fItemVal, Element.ALIGN_LEFT);
            agregarCeldaItem(tItems, fechaHora, fItemVal, Element.ALIGN_CENTER);
            agregarCeldaItem(tItems, "S/ 80.00", fItemNeg, Element.ALIGN_RIGHT);
            document.add(tItems);

            // ── TOTALES ───────────────────────────────────────────────────
            PdfPTable tTotales = new PdfPTable(new float[]{60f, 40f});
            tTotales.setWidthPercentage(60);
            tTotales.setHorizontalAlignment(Element.ALIGN_RIGHT);
            tTotales.setSpacingAfter(4f);

            Font fTotalLabel = new Font(Font.HELVETICA, 8.5f, Font.NORMAL, COLOR_GRIS_TEXTO);
            Font fTotalVal   = new Font(Font.HELVETICA, 8.5f, Font.BOLD, COLOR_VERDE_OSCURO);
            Font fTotalGrand = new Font(Font.HELVETICA, 11f, Font.BOLD, COLOR_BLANCO);

            agregarFilaTotales(tTotales, "Subtotal (sin IGV):",
                    String.format("S/ %.2f", subtotal), fTotalLabel, fTotalVal);
            agregarFilaTotales(tTotales, "IGV (18%):",
                    String.format("S/ %.2f", igv), fTotalLabel, fTotalVal);

            PdfPCell cTotalLabel = new PdfPCell(new Phrase("TOTAL:", fTotalGrand));
            cTotalLabel.setBackgroundColor(COLOR_VERDE_OSCURO);
            cTotalLabel.setBorder(Rectangle.NO_BORDER);
            cTotalLabel.setPadding(8f);
            cTotalLabel.setHorizontalAlignment(Element.ALIGN_LEFT);

            // Monto: solo el número en la fila TOTAL
            PdfPCell cTotalVal = new PdfPCell(new Phrase(
                    String.format("S/ %.2f", montoTotal), fTotalGrand));
            cTotalVal.setBackgroundColor(COLOR_VERDE_OSCURO);
            cTotalVal.setBorder(Rectangle.NO_BORDER);
            cTotalVal.setPadding(8f);
            cTotalVal.setHorizontalAlignment(Element.ALIGN_RIGHT);

            tTotales.addCell(cTotalLabel);
            tTotales.addCell(cTotalVal);
            document.add(tTotales);

            // ── MONTO EN LETRAS (debajo del total, ancho 60% alineado a la derecha) ───
            PdfPTable tLetras = new PdfPTable(1);
            tLetras.setWidthPercentage(60);
            tLetras.setHorizontalAlignment(Element.ALIGN_RIGHT);
            tLetras.setSpacingAfter(12f);

            Font fLetras = new Font(Font.HELVETICA, 8.5f, Font.ITALIC, COLOR_VERDE_OSCURO);
            PdfPCell cLetras = new PdfPCell(new Phrase(montoEnLetras, fLetras));
            cLetras.setBackgroundColor(new Color(0xf0, 0xf5, 0xe0));
            cLetras.setBorder(Rectangle.BOX);
            cLetras.setBorderColor(COLOR_VERDE_MEDIO);
            cLetras.setBorderWidth(1f);
            cLetras.setPadding(7f);
            cLetras.setHorizontalAlignment(Element.ALIGN_LEFT);
            tLetras.addCell(cLetras);
            document.add(tLetras);

            // ── MÉTODO DE PAGO ────────────────────────────────────────────
            PdfPTable tPago = new PdfPTable(1);
            tPago.setWidthPercentage(100);
            tPago.setSpacingAfter(20f);

            Font fPagoVal = new Font(Font.HELVETICA, 9f, Font.NORMAL, COLOR_GRIS_TEXTO);
            PdfPCell cPago = new PdfPCell();
            cPago.setBorder(Rectangle.BOX);
            cPago.setBorderColor(COLOR_VERDE_CLARO);
            cPago.setPadding(10f);
            cPago.addElement(new Paragraph("Método de pago: " +
                    (metodoPago != null ? metodoPago : "—"), fPagoVal));
            cPago.addElement(new Paragraph("Paciente atendido: " + nombrePaciente, fPagoVal));
            tPago.addCell(cPago);
            document.add(tPago);

            // ── FOOTER: separador + QR centrado ─────────────────────────────────
            PdfPTable tSeparador = new PdfPTable(1);
            tSeparador.setWidthPercentage(100);
            tSeparador.setSpacingBefore(4f);
            tSeparador.setSpacingAfter(10f);
            PdfPCell cSep = new PdfPCell(new Phrase(" "));
            cSep.setBorder(Rectangle.BOTTOM);
            cSep.setBorderColor(COLOR_VERDE_MEDIO);
            cSep.setBorderWidth(1.5f);
            cSep.setPadding(0f);
            tSeparador.addCell(cSep);
            document.add(tSeparador);

            // QR centrado en el footer (solo imagen, sin texto)
            if (qrBytes != null) {
                Image qrFooter = Image.getInstance(qrBytes);
                qrFooter.scaleToFit(90f, 90f);
                qrFooter.setAlignment(Image.ALIGN_CENTER);
                document.add(qrFooter);
            }

            Font fFooterBody = new Font(Font.HELVETICA, 7.5f, Font.NORMAL, new Color(0x88, 0x88, 0x88));
            Paragraph pFooter = new Paragraph(
                "Representación impresa de " + tipoLabel + " - Autorizado por SUNAT  ·  © 2026 Medikids",
                fFooterBody);
            pFooter.setAlignment(Element.ALIGN_CENTER);
            pFooter.setSpacingBefore(6f);
            document.add(pFooter);


            document.close();
            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error al generar el comprobante PDF: " + e.getMessage(), e);
        }
    }

    // ── Generación del QR en bytes PNG ───────────────────────────────────────

    private byte[] generarQrBytes(String contenido, int tamanio) {
        try {
            QRCodeWriter writer = new QRCodeWriter();
            Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            hints.put(EncodeHintType.MARGIN, 1);

            BitMatrix matrix = writer.encode(contenido, BarcodeFormat.QR_CODE, tamanio, tamanio, hints);
            BufferedImage image = MatrixToImageWriter.toBufferedImage(matrix);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageIO.write(image, "PNG", out);
            return out.toByteArray();
        } catch (WriterException | java.io.IOException e) {
            return null;
        }
    }

    // ── Conversión de número a letras (formato peruano estándar) ─────────────

    private String numeroALetras(double monto) {
        long parteEntera = (long) monto;
        long centavos    = Math.round((monto - parteEntera) * 100);
        String letras    = convertirEntero(parteEntera);
        return "Son: (" + letras + " SOLES CON " + String.format("%02d", centavos) + "/100)";
    }

    private String convertirEntero(long numero) {
        if (numero == 0) return "CERO";
        if (numero < 0)  return "MENOS " + convertirEntero(-numero);

        String[] unidades = {"", "UN", "DOS", "TRES", "CUATRO", "CINCO",
                             "SEIS", "SIETE", "OCHO", "NUEVE", "DIEZ",
                             "ONCE", "DOCE", "TRECE", "CATORCE", "QUINCE",
                             "DIECISÉIS", "DIECISIETE", "DIECIOCHO", "DIECINUEVE"};
        String[] decenas  = {"", "DIEZ", "VEINTE", "TREINTA", "CUARENTA", "CINCUENTA",
                             "SESENTA", "SETENTA", "OCHENTA", "NOVENTA"};
        String[] centenas = {"", "CIENTO", "DOSCIENTOS", "TRESCIENTOS", "CUATROCIENTOS",
                             "QUINIENTOS", "SEISCIENTOS", "SETECIENTOS", "OCHOCIENTOS", "NOVECIENTOS"};

        if (numero < 20) return unidades[(int) numero];
        if (numero < 100) {
            long dec = numero / 10;
            long uni = numero % 10;
            if (numero < 30 && uni != 0) return "VEINTI" + unidades[(int) uni].toLowerCase();
            return decenas[(int) dec] + (uni != 0 ? " Y " + unidades[(int) uni] : "");
        }
        if (numero == 100) return "CIEN";
        if (numero < 1000) {
            long cen = numero / 100;
            long res = numero % 100;
            return centenas[(int) cen] + (res != 0 ? " " + convertirEntero(res) : "");
        }
        if (numero < 2000) {
            long res = numero % 1000;
            return "MIL" + (res != 0 ? " " + convertirEntero(res) : "");
        }
        if (numero < 1_000_000) {
            long miles = numero / 1000;
            long res   = numero % 1000;
            return convertirEntero(miles) + " MIL" + (res != 0 ? " " + convertirEntero(res) : "");
        }
        if (numero < 2_000_000) {
            long res = numero % 1_000_000;
            return "UN MILLÓN" + (res != 0 ? " " + convertirEntero(res) : "");
        }
        long millones = numero / 1_000_000;
        long res      = numero % 1_000_000;
        return convertirEntero(millones) + " MILLONES" + (res != 0 ? " " + convertirEntero(res) : "");
    }

    // ── Helpers privados ──────────────────────────────────────────────────────

    private Paragraph centrado(String texto, Font font) {
        Paragraph p = new Paragraph(texto, font);
        p.setAlignment(Element.ALIGN_CENTER);
        return p;
    }

    private void agregarFila(PdfPTable tabla, String label, String valor, Font fLabel, Font fValor) {
        PdfPCell cLabel = new PdfPCell(new Phrase(label, fLabel));
        cLabel.setBorder(Rectangle.NO_BORDER);
        cLabel.setPaddingBottom(4f);
        PdfPCell cValor = new PdfPCell(new Phrase(valor != null ? valor : "—", fValor));
        cValor.setBorder(Rectangle.NO_BORDER);
        cValor.setPaddingBottom(4f);
        tabla.addCell(cLabel);
        tabla.addCell(cValor);
    }

    private void agregarCeldaHeader(PdfPTable tabla, String texto, Font font, Color bgColor) {
        PdfPCell cell = new PdfPCell(new Phrase(texto, font));
        cell.setBackgroundColor(bgColor);
        cell.setBorderColor(Color.WHITE);
        cell.setPadding(6f);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        tabla.addCell(cell);
    }

    private void agregarCeldaItem(PdfPTable tabla, String texto, Font font, int align) {
        PdfPCell cell = new PdfPCell(new Phrase(texto, font));
        cell.setBorderColor(new Color(0xee, 0xee, 0xee));
        cell.setPadding(7f);
        cell.setHorizontalAlignment(align);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        tabla.addCell(cell);
    }

    private void agregarFilaTotales(PdfPTable tabla, String label, String valor,
                                    Font fLabel, Font fValor) {
        PdfPCell cL = new PdfPCell(new Phrase(label, fLabel));
        cL.setBorder(Rectangle.BOTTOM);
        cL.setBorderColor(new Color(0xee, 0xee, 0xee));
        cL.setPadding(5f);
        PdfPCell cV = new PdfPCell(new Phrase(valor, fValor));
        cV.setBorder(Rectangle.BOTTOM);
        cV.setBorderColor(new Color(0xee, 0xee, 0xee));
        cV.setPadding(5f);
        cV.setHorizontalAlignment(Element.ALIGN_RIGHT);
        tabla.addCell(cL);
        tabla.addCell(cV);
    }
}
