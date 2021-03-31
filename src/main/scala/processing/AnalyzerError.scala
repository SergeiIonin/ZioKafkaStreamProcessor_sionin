package processing

import scala.util.control.NoStackTrace

sealed trait AnalyzerError extends NoStackTrace

object AnalyzerError {
  case object ReportError extends AnalyzerError
  //case object ResponseExtractionError extends Errors
}
