package com.UAPP.ProjectApplication.service;

import com.mongodb.client.gridfs.model.GridFSFile;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.io.IOException;

@Service
public class FileService {

    private final GridFsTemplate gridFsTemplate;

    @Autowired
    public FileService(GridFsTemplate gridFsTemplate) {
        this.gridFsTemplate = gridFsTemplate;
    }

    public String storeFile(MultipartFile file) throws IOException {
        ObjectId id = gridFsTemplate.store(file.getInputStream(), file.getOriginalFilename(), file.getContentType());
        return id.toHexString();
    }

    public GridFsResource getResource(String id) {
        try {
            GridFSFile gfile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(new ObjectId(id))));
            if (gfile == null) return null;
            return gridFsTemplate.getResource(gfile);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    public void deleteById(String id) {
        try {
            gridFsTemplate.delete(Query.query(Criteria.where("_id").is(new ObjectId(id))));
        } catch (IllegalArgumentException ignored) {}
    }
}
