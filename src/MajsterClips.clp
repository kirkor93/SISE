(deftemplate bot
	(slot hitPoints (default 15))
	(slot actionPoints (default 5))
	(slot woodPoints (default 0))
	(slot state (default initial))
	(slot direction))
	
(deffacts startup
	(bot))
	
(defrule goUp
	(bot (hitPoints ?hp) (woodPoints ?wp))
	(test (eq ?wp 0))
	=>
	(assert (bot (hitPoints ?hp) (direction up))))
	
(defrule goDown
	(bot (hitPoints ?hp))
	(test (< ?hp 5))
	=>
	(assert (bot (hitPoints ?hp) (direction down))))