package com.example.group4_app;

import android.content.Context;
import android.util.Log;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class MySQLConnector {

    private Context context;

    public MySQLConnector(Context context) {
        this.context = context;
    }
    boolean userExists = false;
    public boolean checkUser(String email, String password) {
        String url = "http://127.0.0.1/platfform/check_user.php"; // URL của script PHP để kiểm tra người dùng


        // Tạo một RequestQueue
        RequestQueue queue = Volley.newRequestQueue(context);

        // Tạo một yêu cầu POST
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Xử lý phản hồi từ server (nếu cần)
                        if (response.equals("true")) {
                            userExists = true;
                        } else {
                            userExists = false;
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Xử lý lỗi (nếu cần)
                userExists = false;
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                // Đặt các tham số của yêu cầu POST tại đây (email, password)
                params.put("email", email);
                params.put("password", password);
                return params;
            }
        };

        // Thêm yêu cầu vào hàng đợi
        queue.add(stringRequest);

        return userExists;
    }
    boolean emailExists = false;

    public boolean checkEmailExistence(String email) {
        String url = "http://127.0.0.1/platfform/check_email.php"; // URL của script PHP để kiểm tra email

        // Tạo một RequestQueue
        RequestQueue queue = Volley.newRequestQueue(context);

        // Tạo một yêu cầu POST
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Xử lý phản hồi từ server (nếu cần)
                        emailExists = response.equals("true");
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Xử lý lỗi (nếu cần)
                emailExists = false;
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                // Đặt các tham số của yêu cầu POST tại đây (email)
                params.put("email", email);
                return params;
            }
        };

        // Thêm yêu cầu vào hàng đợi
        queue.add(stringRequest);

        return emailExists;
    }

    interface UserCheckListener {
        void onUserExists(boolean exists);

        void onUserCheckError(String errorMessage);
    }

    public void addUser(User user) {
        String url = "http://127.0.0.1/platfform/add_user.php"; // URL của script PHP để thêm người dùng

        // Tạo một RequestQueue
        RequestQueue queue = Volley.newRequestQueue(context);

        // Tạo một yêu cầu POST
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Xử lý phản hồi từ server (nếu cần)
                        Log.d("AddUserResponse", response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Xử lý lỗi (nếu cần)
                Log.e("AddUserError", error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                // Đặt các tham số của yêu cầu POST tại đây (tên, email, password)
                params.put("name", user.getName());
                params.put("email", user.getEmail());
                params.put("password", user.getPassword());
                return params;
            }
        };

        // Thêm yêu cầu vào hàng đợi
        queue.add(stringRequest);
    }

    public void getAllUsers(final UserFetchListener listener) {
        String url = "http://127.0.0.1/platfform/get_all_users.php"; // URL của script PHP để lấy danh sách người dùng

        // Tạo một RequestQueue
        RequestQueue queue = Volley.newRequestQueue(context);

        // Tạo một yêu cầu GET
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Xử lý phản hồi từ server (nếu cần)
                        List<User> userList = new ArrayList<>();
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                int userId = jsonObject.getInt("user_id");
                                String userName = jsonObject.getString("user_name");
                                String userEmail = jsonObject.getString("user_email");
                                String userPassword = jsonObject.getString("user_password");
                                userList.add(new User(userId, userName, userEmail, userPassword));
                            }
                            listener.onUserFetchSuccess(userList);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            listener.onUserFetchFailure("Error parsing JSON");
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Xử lý lỗi (nếu cần)
                listener.onUserFetchFailure(error.toString());
            }
        });

        // Thêm yêu cầu vào hàng đợi
        queue.add(stringRequest);
    }

    interface UserFetchListener {
        void onUserFetchSuccess(List<User> userList);

        void onUserFetchFailure(String errorMessage);
    }
}
