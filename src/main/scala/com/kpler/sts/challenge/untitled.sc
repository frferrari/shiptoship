

List(1, 2, 3).combinations(2).toList.flatten

/*
val myList = List(1, 2, 3, 4, 5)

val t = for {
  l1 <- myList
  l2 <- myList.filter(_ != l1)
} yield (l1, l2)

def reject(tuples: List[(Int, Int)])(c: (Int, Int)): Boolean = {
  val b = c._1 > c._2 && tuples.contains((c._2, c._1))
  b
}

t
t.filterNot(reject(t))
*/