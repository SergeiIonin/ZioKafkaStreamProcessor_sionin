package messaging

import com.google.common.hash.{BloomFilter, Funnel, Funnels}

trait BloomFiltered[T] {

  def bloomFilter: BloomFilter[T]

  def updateBloomFilter(update: T): BloomFilter[T] = {
    bloomFilter.put(update)
    bloomFilter
  }

  def multipleBloomFilterUpdate(updates: Seq[T]): BloomFilter[T] = {
    updates foreach {
      elem => updateBloomFilter(elem)
    }
    bloomFilter
  }

}

object BloomFiltered {
  def initEmptyBloomFilter[T](expectedInsertions: Int, falseProbability: Double)(implicit funnel: Funnel[T]): BloomFilter[T] = {
    BloomFilter.create(funnel, expectedInsertions, falseProbability)
  }
}
