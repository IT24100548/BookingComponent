package servlet;

import handler.BookingFileHandler;
import model.Booking;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;

@WebServlet("/BookingUpdateServlet")
public class BookingUpdateServlet extends HttpServlet {
    private static final String PACKAGE_FILE =
            "C:\\Users\\ASUS\\OneDrive\\Desktop\\UpdatedBookingManager\\BookingManager\\src\\main\\resources\\packages.txt";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String bookingId = request.getParameter("bookingId");
        Booking bookingToEdit = null;
        for (Booking b : BookingFileHandler.getAllBookings()) {
            if (b.getBookingId().equals(bookingId)) {
                bookingToEdit = b;
                break;
            }
        }
        if (bookingToEdit == null) {
            response.sendRedirect(request.getContextPath() + "/BookingHistoryServlet");
            return;
        }

        // Build package <option>s
        StringBuilder opts = new StringBuilder();
        try (BufferedReader rd = new BufferedReader(new FileReader(PACKAGE_FILE))) {
            String ln;
            while ((ln = rd.readLine()) != null) {
                String[] p = ln.split(",", 5);
                if (p.length >= 4) {
                    String name = p[1], price = p[3];
                    opts.append("<option value=\"")
                            .append(name).append("\"")
                            .append(name.equals(bookingToEdit.getPackageId())
                                    ? " selected"
                                    : "")
                            .append(">")
                            .append(name).append(" – ").append(price)
                            .append("</option>");
                }
            }
        }

        request.setAttribute("bookingToEdit", bookingToEdit);
        request.setAttribute("packageOptions", opts.toString());
        request.getRequestDispatcher("BookingUpdate.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String bookingId = request.getParameter("bookingId");
        String fullName = request.getParameter("fullName");
        String phoneNumber = request.getParameter("phoneNumber");
        String address = request.getParameter("address");
        String gender = request.getParameter("gender");
        String email = request.getParameter("email");
        String packageName = request.getParameter("packageName");
        String status = request.getParameter("status");
        String specialReq = request.getParameter("specialRequirements");
        int numberOfPeople = Integer.parseInt(request.getParameter("numberOfPeople"));

        // Recompute unit price
        double pricePerUnit = 0;
        try (BufferedReader rd = new BufferedReader(new FileReader(PACKAGE_FILE))) {
            String ln;
            while ((ln = rd.readLine()) != null) {
                String[] p = ln.split(",", 5);
                if (p.length >= 4 && p[1].equals(packageName)) {
                    pricePerUnit = Double.parseDouble(p[3]);
                    break;
                }
            }
        }
        double totalPrice = pricePerUnit * numberOfPeople;

        boolean found = false;
        List<Booking> list = BookingFileHandler.getAllBookings();
        for (Booking b : list) {
            if (b.getBookingId().equals(bookingId)) {
                b.setFullName(fullName);
                b.setPhoneNumber(phoneNumber);
                b.setAddress(address);
                b.setGender(gender);
                b.setEmail(email);
                b.setPackageId(packageName);
                b.setStatus(status);
                b.setSpecialRequirements(specialReq);
                b.setNumberOfPeople(numberOfPeople);
                b.setTotalPrice(totalPrice);
                BookingFileHandler.updateBooking(b);
                found = true;
                break;
            }
        }

        response.setContentType("text/html");
        try (PrintWriter out = response.getWriter()) {
            if (found) {
                out.println("<h2>Booking updated!</h2>");
                out.printf("<p>New total price: %.2f</p>", totalPrice);
            } else {
                out.println("<h2 style='color:red;'>Booking not found.</h2>");
            }
            out.println("<a href=\"" + request.getContextPath()
                    + "/BookingHistoryServlet\">Back to History</a>");
        }
    }
}
