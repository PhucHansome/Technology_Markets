package com.cg.controller;

import model.User;
import service.IUserService;
import service.UserService;
import utils.UploadImage;
import utils.ValidateUtils;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "CPUserServlet", urlPatterns = "/cp/user")
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 2, // 2MB
        maxFileSize = 1024 * 1024 * 50, // 50MB
        maxRequestSize = 1024 * 1024 * 50) // 50MB
public class CPUserServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    IUserService userService;

    @Override
    public void init() throws ServletException {
        userService = new UserService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");

        if (action == null) {
            action = "";
        }
        switch (action) {
            case "create":
                showCreatePage(req, resp);
                break;
            case "edit":
                showEditForm(req, resp);
                break;
            case "list":
                showListPage(req, resp);
                break;
            case "viewdetail":
                doView(req, resp);
                break;
            case "search":
                searchByName(req, resp);
                break;
            default:
                searchByName(req, resp);
                break;
        }
    }

    private void searchByName(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<User> userList = null;
        String query = "";
        if (req.getParameter("search") != null) {
            query = req.getParameter("search");

            userList = userService.findNameUser(query);
        } else {
            userList = userService.findAll();
        }
        req.setAttribute("userList", userList);
        RequestDispatcher dispatcher = req.getRequestDispatcher("/cp/user/List.jsp");
        dispatcher.forward(req, resp);
    }

    private void showEditForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        long userId = Long.parseLong(req.getParameter("id"));
        User user = userService.findById(userId);
        List<String> errors = new ArrayList<>();
        if (!userService.existByUserId(userId)) {
            errors.add("UserId kh??ng t???n t???i");
        }
        if (errors.size() > 0) {
            req.setAttribute("errors", errors);
        }
        RequestDispatcher dispatcher = req.getRequestDispatcher("/cp/user/Edit.jsp");
        req.setAttribute("user", user);
        dispatcher.forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html/charset=UTF-8");
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        String action = req.getParameter("action");

        if (action == null) {
            action = "";
        }
        switch (action) {
            case "create":
                String imageName = UploadImage.uploadImages(req, resp);
                doCreate(req, resp, imageName);
                break;
            case "edit":
                imageName = UploadImage.uploadImages(req, resp);
                doUpdate(req, resp, imageName);
                break;
            case "viewdetail":
                doView(req, resp);
                break;
            default:
            case "search":
                searchByName(req, resp);
                break;
        }

    }

    private void doView(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        long userId = Long.parseLong(req.getParameter("id"));
        User user = userService.findFullById(userId);
        RequestDispatcher dispatcher = req.getRequestDispatcher("/cp/user/Viewdetail.jsp");
        req.setAttribute("user", user);
        dispatcher.forward(req, resp);
    }

    private void doUpdate(HttpServletRequest req, HttpServletResponse resp, String img) throws ServletException, IOException {
        User updateUser = null;
        RequestDispatcher dispatcher = req.getRequestDispatcher("/cp/user/Edit.jsp");
        long userId = Long.parseLong(req.getParameter("id").trim());
        String fullName = req.getParameter("fullName").trim();
        String phone = req.getParameter("phone").trim();
        String address = req.getParameter("address").trim();
        List<String> errors = new ArrayList<>();
        boolean isPhone = ValidateUtils.isNumberPhoneVailid(phone);
        if (fullName.isEmpty() ||
                phone.isEmpty() ||
                address.isEmpty()) {
            errors.add("H??y nh???p ?????y ????? th??ng tin");
        }
//        try {
//           userId = Long.parseLong(userIdRaw);
        updateUser = new User(userId, fullName, phone, address, img);
//        }catch (NumberFormatException e){
//            errors.add("ID kh??ng t???n t???i");
//        }
        if (fullName.isEmpty()) {
            errors.add("Full name kh??ng ???????c ????? tr???ng");
        }
        if (phone.isEmpty()) {
            errors.add("Phone kh??ng ???????c ????? tr???ng");
        }
        if (!isPhone) {
            errors.add("Phone kh??ng ????ng ?????nh d???ng");
        }
        if (address.isEmpty()) {
            errors.add("Address kh??ng ???????c ????? tr???ng");
        }
        if (!img.contains(".jpg") && !img.contains(".png")) {
            errors.add("?????nh d???ng file ???nh kh??ng ????ng vui l??ng ch???n l???i");
        }

        if (errors.size() == 0) {
            updateUser = new User(userId, fullName, phone, address, img);
            boolean success = false;
            success = userService.update(updateUser);
            if (success) {
                req.setAttribute("success", true);
            } else {
                req.setAttribute("errors", true);
                errors.add("Invalid data, Please check again!");
            }
        }
        if (errors.size() > 0) {
            req.setAttribute("errors", errors);
            req.setAttribute("user", updateUser);
        }
        dispatcher.forward(req, resp);
    }


    private void doCreate(HttpServletRequest req, HttpServletResponse resp, String imageName) throws
            ServletException, IOException {
        User user;
        RequestDispatcher dispatcher = req.getRequestDispatcher("/cp/user/Create.jsp");
        String userName = req.getParameter("userName").replaceAll(" ", "").toLowerCase();
        String password = req.getParameter("password").trim();
        String fullName = req.getParameter("fullName").trim();
        String phone = req.getParameter("phone").trim();
        String email = req.getParameter("email").trim();
        String address = req.getParameter("address").trim();
        String Role = req.getParameter("role").trim();
        String img = imageName.trim();

        List<String> errors = new ArrayList<>();
        boolean isPassword = ValidateUtils.isPasswordVailid(password);
        boolean isPhone = ValidateUtils.isNumberPhoneVailid(phone);
        boolean isEmail = ValidateUtils.isEmailValid(email);
//        boolean isUserName = ValidateUtils.isUserNameVailid(userName);

        user = new User(userName, password, fullName, phone, email, address, Role, imageName);
        if (userName.isEmpty() ||
                password.isEmpty() ||
                fullName.isEmpty() ||
                phone.isEmpty() ||
                email.isEmpty() ||
                address.isEmpty() ||
                Role.isEmpty() ||
                img.isEmpty()) {
            errors.add("Vui l??ng ??i???n ?????y ????? th??ng tin");
        }
        if (userName.isEmpty()) {
            errors.add("UserName kh??ng ???????c ????? tr???ng");
        }
        if (password.isEmpty()) {
            errors.add("Password kh??ng ???????c ????? tr???ng");
        }
        if (fullName.isEmpty()) {
            errors.add("Fullname kh??ng ???????c ????? tr???ng");
        }
        if (phone.isEmpty()) {
            errors.add("Phone Nh???p v??o kh??ng ????ng");
        }
        if (!isPhone) {
            errors.add("Phone kh??ng ????ng ?????nh d???ng");
        }
        if (email.isEmpty()) {
            errors.add("Email nh???p v??o kh??ng ????ng");
        }
        if (!isEmail) {
            errors.add("Email nh???p v??o kh??ng ????ng d???nh d???ng");
        }
        if (address.isEmpty()) {
            errors.add("Address kh??ng ???????c ????? tr???ng");
        }
        if (Role.isEmpty()) {
            errors.add("Role kh??ng ???????c ch???n");
        }
        if (img.isEmpty()) {
            errors.add("URL kh??ng ???????c ????? tr???ng");
        }
        if (userService.existsByEmail(email)) {
            errors.add("Email ???? t???n t???i");
        }
        if (!Role.equals("ADMIN") && !Role.equals("USER")) {
            errors.add("Role ph???i l?? ADMIN ho???c USER");
        }
//        if (!isUserName) {
//            errors.add("UserName kh??ng ????ng ?????nh d???ng (kh??ng c?? kho???ng c??ch) ");
//        }
        if (userService.existByUsername(userName)) {
            errors.add("Username n??y ???? t???n t???i!");
        }
        if (!isPassword) {
            errors.add("Password kh??ng ????ng ?????nh d???ng");
        }
        if (!imageName.contains(".jpg")) {
            errors.add("?????nh d???ng file ???nh kh??ng ????ng vui l??ng ch???n l???i");
        }
        if (errors.size() == 0) {
            user = new User(userName, password, fullName, phone, email, address, Role, imageName);
            boolean success = false;
            success = userService.create(user);

            if (success) {
                req.setAttribute("success", true);
            } else {
                req.setAttribute("errors", true);
                errors.add("D??? li???u kh??ng h???p l???, Vui l??ng ki???m tra l???i!");
            }

        }
        if (errors.size() > 0) {
            req.setAttribute("errors", errors);
            req.setAttribute("userCreate", user);
        }
        dispatcher.forward(req, resp);
    }

    private void showCreatePage(HttpServletRequest req, HttpServletResponse resp) throws
            ServletException, IOException {
        RequestDispatcher dispatcher = req.getRequestDispatcher("/cp/user/Create.jsp");
        List<User> userList = userService.findAll();
        req.setAttribute("userList", userList);
        dispatcher.forward(req, resp);
    }

    private void showListPage(HttpServletRequest req, HttpServletResponse resp) throws
            ServletException, IOException {
        RequestDispatcher dispatcher = req.getRequestDispatcher("/cp/user/List.jsp");
        List<User> userList = userService.findAll();
        req.setAttribute("userList", userList);
        dispatcher.forward(req, resp);
    }
}
