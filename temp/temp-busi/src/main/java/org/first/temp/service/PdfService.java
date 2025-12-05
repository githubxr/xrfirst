package org.first.temp.service;


import org.first.temp.dto.FtlModel;

import java.util.List;

public interface PdfService {
    void genPdfBatchToDir(FtlModel model, List<String> dataList);
    
}
