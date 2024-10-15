package vn.HiepKa.controllers.admin;


import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import vn.HiepKa.entity.Category;
import vn.HiepKa.entity.Video;
import vn.HiepKa.services.ICategoryService;
import vn.HiepKa.services.IVideoService;
import vn.HiepKa.services.Impl.CategoryServiceImpl;
import vn.HiepKa.services.Impl.VideoServiceImpl;
import static vn.HiepKa.utils.Constant.*;

@MultipartConfig(fileSizeThreshold = 1024 * 1024,
maxFileSize = 1024 * 1024 * 5, maxRequestSize = 1024 * 1024 * 5 * 5)
@WebServlet(urlPatterns = { "/admin/videos", "/admin/video/add",
							"/admin/video/insert", "/admin/video/edit", 
							"/admin/video/update", "/admin/video/delete" })
public class VideoController extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private IVideoService videoService = new VideoServiceImpl();
	private ICategoryService categoryService = new CategoryServiceImpl();
	@Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        String url = req.getRequestURI();

        if (url.contains("/admin/videos")) {
            // Hiển thị danh sách video
            List<Video> videos = videoService.findAll();
            req.setAttribute("listVideos", videos);
            req.getRequestDispatcher("/views/admin/video-list.jsp").forward(req, resp);

        } else if (url.contains("/admin/video/add")) {
            // Hiển thị form thêm video
            List<Category> categories = categoryService.findAll();
            req.setAttribute("listCategories", categories);
            req.getRequestDispatcher("/views/admin/video-add.jsp").forward(req, resp);

        } else if (url.contains("/admin/video/edit")) {
            // Hiển thị form chỉnh sửa video
            String videoId = req.getParameter("id");
            Video video = videoService.findById(videoId);
            List<Category> categories = categoryService.findAll();
            req.setAttribute("video", video);
            req.setAttribute("listCategories", categories);
            req.getRequestDispatcher("/views/admin/video-edit.jsp").forward(req, resp);

        } else if (url.contains("/admin/video/delete")) {
            // Xóa video
            String videoId = req.getParameter("id");
            try {
                videoService.delete(videoId);
            } catch (Exception e) {
                e.printStackTrace();
            }
            resp.sendRedirect(req.getContextPath() + "/admin/videos");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        String url = req.getRequestURI();

        if (url.contains("/admin/video/insert")) {
            // Thêm mới video
            String videoId = req.getParameter("videoId");
            String title = req.getParameter("title");
            int views = Integer.parseInt(req.getParameter("views"));
            String description = req.getParameter("description");
            int active = Integer.parseInt(req.getParameter("active"));
            String categoryId = req.getParameter("categoryId");

            // Lấy category từ DB
            Category category = categoryService.findById(Integer.parseInt(categoryId));

            // Xử lý poster (ảnh)
            String uploadPath = DIR;
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdir();
            }
            String poster = "";
            Part part = req.getPart("poster");

            if (part != null && part.getSize() > 0) {
                String filename = Paths.get(part.getSubmittedFileName()).getFileName().toString();
                int index = filename.lastIndexOf(".");
                String ext = filename.substring(index + 1);
                poster = System.currentTimeMillis() + "." + ext;
                part.write(uploadPath + File.separator + poster);
            } else {
                poster = "default.png";
            }

            // Tạo video mới
            Video video = new Video(videoId, active, description, poster, title, views, category);
            videoService.insert(video);

            resp.sendRedirect(req.getContextPath() + "/admin/videos");

        } else if (url.contains("/admin/video/update")) {
            // Cập nhật video hiện tại
            String videoId = req.getParameter("videoId");
            Video video = videoService.findById(videoId);

            // Cập nhật các trường của video
            video.setTitle(req.getParameter("title"));
            video.setViews(Integer.parseInt(req.getParameter("views")));
            video.setDescription(req.getParameter("description"));
            video.setActive(Integer.parseInt(req.getParameter("active")));
            String categoryId = req.getParameter("categoryId");
            video.setCategory(categoryService.findById(Integer.parseInt(categoryId)));

            // Xử lý ảnh (poster)
            String oldPoster = video.getPoster();
            Part part = req.getPart("poster");

            if (part != null && part.getSize() > 0) {
                String filename = Paths.get(part.getSubmittedFileName()).getFileName().toString();
                int index = filename.lastIndexOf(".");
                String ext = filename.substring(index + 1);
                String poster = System.currentTimeMillis() + "." + ext;
                part.write(DIR + File.separator + poster);
                video.setPoster(poster);
            } else {
                video.setPoster(oldPoster);  // Giữ lại ảnh cũ nếu không tải lên ảnh mới
            }

            // Cập nhật video
            videoService.update(video);

            resp.sendRedirect(req.getContextPath() + "/admin/videos");
        }
    }
}
