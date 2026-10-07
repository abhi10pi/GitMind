package gitMind.backend.service.ai;

import java.util.List;

import gitMind.backend.dto.CitationDto;

public record RetrievedContext(
        List<CitationDto> citations,
        String contextText) {
}