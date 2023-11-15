package service.ms_search_engine.controller;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/batchSearch")
public class BatchSpectrumSearchController {

    // post /api/batchSearch/file/upload   -> upload file s3, check file, save file path to db, return task id
    // post /api/batchSearch/task/submit -> submit task, check parameter, save task to db, sent to task queue
    // get /api/batchSearch/task/{id} -> get task status by id
    // delete /api/batchSearch/task/{id} -> delete task by id, change task status to delete, s3 data delete



}
