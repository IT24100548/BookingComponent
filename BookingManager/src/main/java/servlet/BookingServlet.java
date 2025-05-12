package servlet;

import handler.BookingFileHandler;
import model.Booking;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.time.LocalDate;

@WebServlet("/BookingServlet")
public class BookingServlet extends HttpServlet {
    private static final String PACKAGE_FILE =
            "C:\\Users\\ASUS\\OneDrive\\Desktop\\UpdatedBookingManager (2)\\UpdatedBookingManager\\BookingManager\\src\\main\\resources\\packages.txt";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String fullName = request.getParameter("fullName");
        String phoneNumber = request.getParameter("phoneNumber");
        String address = request.getParameter("address");
        String gender = request.getParameter("gender");
        String email = request.getParameter("email");
        String packageName = request.getParameter("packageName");
        String specialReq = request.getParameter("specialRequirements");  // may be null
        int numberOfPeople = Integer.parseInt(request.getParameter("numberOfPeople"));

        // Generate ID & date
        String bookingId = BookingFileHandler.generateBookingId();
        String bookingDate = LocalDate.now().toString();
        String status = "Confirmed";

        // Lookup price per unit
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

        Booking booking = new Booking(
                bookingId, fullName, phoneNumber, address, gender,
                email, packageName, bookingDate, status,
                specialReq,
                numberOfPeople, totalPrice
        );

        BookingFileHandler.addBooking(booking);

        // Set success data and forward to JSP
        request.setAttribute("newTotalPrice", totalPrice);
        request.setAttribute("success", true);
        request.setAttribute("fullName", fullName);
        request.getRequestDispatcher("bookingSuccess.jsp").forward(request, response);
    }
}
