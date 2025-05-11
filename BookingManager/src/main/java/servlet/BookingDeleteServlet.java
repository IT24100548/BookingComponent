package servlet;

import handler.BookingFileHandler;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/BookingDeleteServlet")
public class BookingDeleteServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String bookingId = request.getParameter("bookingId");

        // Delete the booking using BookingFileHandler
        BookingFileHandler.deleteBooking(bookingId);

        // Response
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<h2>Booking deleted successfully!</h2>");
        out.println("<a href='BookingHistoryServlet'>Back to Booking History</a>");
    }
}
