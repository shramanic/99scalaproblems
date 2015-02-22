package mtree

case class MTree[+T](value: T, children: List[MTree[T]]) {
    override def toString = 
        if (!children.isEmpty) 
            "M(" + value.toString + " {" + children.map(_.toString).mkString(",") + "})"
        else
            "M(" + value.toString + ")"

    def nodeCount: Int = 1 + children.map(_.nodeCount).sum

    def toNodeString: String = value.toString + children.map(_.toNodeString).mkString("") + "^"

    // Problem 71
    // The sum of the (depth -1)s of all the trees basically
    def internalPathLength: Int = internalPathLengthImpl(0)

    private def internalPathLengthImpl(myLength: Int): Int = 
        myLength + children.map(_.internalPathLengthImpl(myLength + 1)).sum

    // problem 72
    // Construct the post-order traveral of the tree
    def postOrder: List[T] = 
        children.map(_.postOrder).foldRight(List[T]())((v, accum) => v ::: accum) ::: List(value)

    // problem 73, part 1
    // convert this MTree to a lispy string
    def toLispyString: String = 
        if (children.isEmpty) value.toString
        else "(" + value.toString + " " + children.map(_.toLispyString).mkString(" ") + ")"
}

object MTree {
    def apply[T](value: T):MTree[T] = MTree(value, List())

    // this function assumes that the nodeString was made
    // from a tree of type MTree[Char]
    def fromNodeString(nodeStr: String): MTree[Char] = {
        val toks = nodeStr.toList
        val (mt, toksLeft) = takeTree(toks)
        if (!toksLeft.isEmpty) throw new Exception("Couldn't consume all the tokens while creating MTree")
        mt
    }

    def takeTree(toks0: List[Char]): (MTree[Char], List[Char]) = 
        toks0 match {
            case Nil => throw new Exception("takeTree encountered an empty list")
            case ('^'::_) => throw new Exception("Encountered an unexpected '^'")
            case (v::toks1) => {
                var toks = toks1               

                import scala.collection.mutable
                val buf = new mutable.ListBuffer[MTree[Char]]

                while(toks.head != '^'){
                    val (nextTree, nextToks) = takeTree(toks)
                    buf += nextTree
                    toks = nextToks
                }

                (MTree(v, buf.toList), toks.tail)
            }
        }
}
