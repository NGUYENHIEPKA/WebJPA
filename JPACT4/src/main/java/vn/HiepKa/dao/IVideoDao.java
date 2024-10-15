package vn.HiepKa.dao;

import java.util.List;
import vn.HiepKa.entity.Video;

public interface IVideoDao {
	List<Video> findAll();
    Video findById(String videoId);
    void insert(Video video);
    void update(Video video);
    void delete(String videoId) throws Exception;
}