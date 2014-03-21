package edu.luc.etl.ccacw.sensor

import scalaz.{ Show, Tree, TreeLoc }
import scalaz.std.option._
import scalaz.syntax.tree._

package object model {

  implicit object shr extends Show[Resource] { override def shows(r: Resource) = r.toString }

  implicit object shl extends Show[TreeLoc[Resource]] { override def shows(r: TreeLoc[Resource]) = "->" + r.toString }

//  implicit val r2t = ToTreeOps[Location] _

  implicit def matchResourceByName(res: Resource, name: String) = res.name == name

  // TODO validation of domain model
  // TODO auto-generate routes

  /**
   * Attempts to navigate into the (cojoined) tree of tree locations based on
   * the given selection criterion and path of String elements.
   * Each parent-to-child navigation step is a Kleisli arrow (a -> m[b]),
   * where the monad m is Option and both a and b are Tree[TreeLoc[T]],
   * and succeeds only if the criterion on the argument of type a is satisfied.
   * Using such a Kleisli arrow instead of an ordinary function a -> b allows
   * propagating any failure along the way directly as the final result,
   * bypassing the remaining steps.
   */
  def descend[T](path: Iterable[String])(root: Tree[TreeLoc[T]])(implicit crit: (T, String) => Boolean): Option[Tree[TreeLoc[T]]] =
    path.foldLeft {
      some(root)
    } { (nodeOption, pathElement) =>
      nodeOption flatMap {
        _.subForest find { node => crit(node.rootLabel.getLabel, pathElement) }
      }
    }
}