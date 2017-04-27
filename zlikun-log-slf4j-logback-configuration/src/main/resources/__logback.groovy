import ch.qos.logback.classic.encoder.PatternLayoutEncoder

import static ch.qos.logback.classic.Level.DEBUG
import static ch.qos.logback.classic.Level.INFO

scan("3 seconds")
context.name = "zlikun-log"
appender("STDOUT", ConsoleAppender) {
    encoder(PatternLayoutEncoder) {
        pattern = "%d{yyyy/MM/dd HH:mm:ss} | %contextName | %t | %level | %logger | %msg%n"
    }
}
root(INFO, ["STDOUT"])
logger("com.zlikun.log", DEBUG, ["STDOUT"], false)