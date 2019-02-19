package akka.lease

import akka.annotation.ApiMayChange
import com.typesafe.config.{Config, ConfigValueType}
import akka.util.JavaDurationConverters._
import scala.concurrent.duration._

object TimeoutSettings {
  @ApiMayChange
  def apply(config: Config): TimeoutSettings = {
    val heartBeatTimeout = config.getDuration("heartbeat-timeout").asScala
    val heartBeatInterval = config.getValue("heartbeat-interval").valueType() match {
      case ConfigValueType.STRING if config.getString("heartbeat-interval").isEmpty => (heartBeatTimeout / 10).max(5.seconds)
      case _ => config.getDuration("heartbeat-interval").asScala
    }
    require(heartBeatInterval < (heartBeatTimeout / 2), "heartbeat-interval must be less than half heartbeat-timeout")
    new TimeoutSettings(
      heartBeatInterval,
      heartBeatTimeout,
      config.getDuration("lease-operation-timeout").asScala)
  }

}

@ApiMayChange
final class TimeoutSettings(
  val heartbeatInterval: FiniteDuration,
  val heartbeatTimeout:  FiniteDuration,
  val operationTimeout:  FiniteDuration
) {
  /**
   * Java API
   */
  def getHeartbeatInterval(): java.time.Duration = heartbeatInterval.asJava
  /**
   * Java API
   */
  def getHeartbeatTimeout(): java.time.Duration = heartbeatTimeout.asJava
  /**
   * Java API
   */
  def getOperationTimeout(): java.time.Duration = operationTimeout.asJava
}


