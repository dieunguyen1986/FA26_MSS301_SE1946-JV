package fu.ats.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Embeddable
public class InterviewFeedback {

    @Column(name = "feedback_text", columnDefinition = "TEXT")
    private String feedbackText;

    private Integer score;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private InterviewResult result;

    @Column(name = "interviewer_name", length = 255)
    private String interviewerName;

    @Column(name = "interviewer_email", length = 255)
    private String interviewerEmail;

    @Column(name = "notes_file_path", length = 1000)
    private String notesFilePath;
}
