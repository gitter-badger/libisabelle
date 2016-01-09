theory Refined
imports Protocol Main
begin

ML\<open>
fun try_timeout secs f x =
  let
    val time = Time.fromSeconds secs
  in
    Exn.capture (TimeLimit.timeLimit time f) x
  end

fun prove t =
  let
    val _ = Goal.parallel_proofs := 0
    fun tac {context, ...} = auto_tac context
  in
    try_timeout 5 (Goal.prove @{context} [] [] (HOLogic.mk_Trueprop t)) tac
    |> Exn.get_res
    |> is_some
  end
\<close>

operation_setup check = \<open>
  {from_lib = Codec.term,
   to_lib = Codec.bool,
   action = prove}\<close>

end