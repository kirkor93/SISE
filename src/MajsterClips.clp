(deftemplate bot
	(slot hitPoints (default 15))
	(slot actionPoints (default 5))
	(slot woodPoints (default 0))
	(slot state (default initial))
	(slot direction)
	(slot currentField (default normal)))
	
(deftemplate tile
	(slot x)
	(slot y)
	(slot type (default current))
	(slot fieldType (default normal))) 
	
(deffacts startup
	(bot)
	(tile))
	
(defrule goUp
	(bot (hitPoints ?hp) (actionPoints ?ap))
	(test (> ?ap 0))
	=>
	(assert (bot (hitPoints ?hp) (state current) (direction up))))
	
;(defrule goDown
;	(bot (hitPoints ?hp))
;	(test (< ?hp 5))
;	=>
;	(assert (bot (hitPoints ?hp) (direction down))))