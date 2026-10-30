package fu.ats.messaging;

import fu.ats.dto.CvAnalysisRequest;
import fu.ats.dto.CvAnalysisResponse;
import fu.ats.service.CvParseService;
import fu.ats.shared.commonweb.messaging.rabbitmq.CvParseCommand;
import fu.ats.shared.commonweb.messaging.rabbitmq.CvParseResultEvent;
import fu.ats.shared.commonweb.messaging.rabbitmq.RabbitMessagingNames;
import fu.ats.storage.MinioCvFileReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class CvParseMessageListener {

    private final CvParseService cvParseService;
    private final MinioCvFileReader fileReader;
    private final RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = RabbitMessagingNames.CV_PARSE_QUEUE)
    public void handle(CvParseCommand command) {
        log.info(
                "Received CV parse command messageId={}, cvId={}, filePath={}",
                command.messageId(),
                command.cvId(),
                command.filePath()
        );

        try {
            String cvText = fileReader.readText(command.filePath());
            String fileName = fileReader.fileName(command.filePath());

            CvAnalysisResponse analysis = cvParseService.analyze(
                    new CvAnalysisRequest(command.candidateId(), fileName, cvText)
            );

            publishResult(CvParseResultEvent.success(command, toParsedData(analysis)));

            log.info(
                    "CV parse completed messageId={}, cvId={}",
                    command.messageId(),
                    command.cvId()
            );
        } catch (Exception exception) {
            log.error(
                    "CV parse failed messageId={}, cvId={}, filePath={}",
                    command.messageId(),
                    command.cvId(),
                    command.filePath(),
                    exception
            );

            // Publish a failure result and acknowledge the command. This keeps
            // one bad file from being redelivered forever in the demo.
            publishResult(CvParseResultEvent.failure(
                    command,
                    "CV_PARSE_FAILED",
                    exception.getMessage()
            ));
        }
    }

//    try {
//        process(command);
//    } catch (Exception exception) {
//        throw new AmqpRejectAndDontRequeueException(
//                "CV processing failed",
//                exception
//        );
//    }

    private void publishResult(CvParseResultEvent result) {
        rabbitTemplate.convertAndSend(
                RabbitMessagingNames.CV_EXCHANGE,
                RabbitMessagingNames.CV_RESULT_ROUTING_KEY,
                result
        );
    }

    private Map<String, Object> toParsedData(CvAnalysisResponse analysis) {
        Map<String, Object> parsedData = new LinkedHashMap<>();
        parsedData.put("candidateId", analysis.candidateId());
        parsedData.put("fileName", analysis.fileName());
        parsedData.put("email", analysis.email());
        parsedData.put("phone", analysis.phone());
        parsedData.put("skills", analysis.skills());
        parsedData.put("languages", analysis.languages());
        parsedData.put("education", analysis.education());
        parsedData.put("yearsOfExperience", analysis.yearsOfExperience());
        parsedData.put("summary", analysis.summary());
        parsedData.put("status", analysis.status());
        return parsedData;
    }
}
