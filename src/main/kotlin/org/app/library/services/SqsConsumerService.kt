package org.app.library.services
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener
import org.springframework.stereotype.Service

@Service
class SqsConsumerService {

//    @SqsListener("\${aws.sqs.queueUrl}")
//    fun receiveMessage(message: String) {
//        println("Received message: $message")
//    }
}