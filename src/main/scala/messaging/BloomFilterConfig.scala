package messaging

trait BloomFilterConfig {
  def expectedInsertions: Int = 100_000
  def falseProbability: Double = 0.001
}
