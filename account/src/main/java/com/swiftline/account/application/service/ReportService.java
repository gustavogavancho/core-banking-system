package com.swiftline.account.application.service;

import java.time.LocalDateTime;

public interface ReportService {
    com.swiftline.account.application.dto.ReportAccountStateResponse generate(Long clientId, LocalDateTime from, LocalDateTime to);
}
