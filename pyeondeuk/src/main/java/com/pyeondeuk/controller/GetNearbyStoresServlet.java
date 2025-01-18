package com.pyeondeuk.controller;

import com.google.gson.Gson;
import com.pyeondeuk.model.ConvenienceStoreDTO;
import com.pyeondeuk.controller.NearbyStoreService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/getNearbyStores")
public class GetNearbyStoresServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        double latitude, longitude, radius;

        System.out.println("서블릿 요청 들어옴");

        try {
            latitude = Double.parseDouble(request.getParameter("latitude"));
            longitude = Double.parseDouble(request.getParameter("longitude"));
            radius = Double.parseDouble(request.getParameter("radius"));
        } catch (NumberFormatException e) {
            System.out.println("Invalid parameters: " + e.getMessage());
            response.setContentType("application/json; charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().print("{\"error\":\"Invalid parameters. Please check latitude, longitude, and radius.\"}");
            return;
        }

        NearbyStoreService service = new NearbyStoreService();
        List<ConvenienceStoreDTO> stores;

        try {
            stores = service.getNearbyStores(latitude, longitude, radius);
        } catch (Exception e) {
            System.out.println("서비스 호출 중 예외 발생: " + e.getMessage());
            e.printStackTrace();
            response.setContentType("application/json; charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().print("{\"error\":\"Internal server error. Please try again later.\"}");
            return;
        }

        response.setContentType("application/json; charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.print(new Gson().toJson(stores));
            out.flush();
        }
        
      
    }
    
    
}

