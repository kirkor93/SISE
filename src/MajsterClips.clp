(deftemplate bot
	(slot hitPoints (default 15))
	(slot actionPoints (default 5))
	(slot woodPoints (default 0)))
	
(defglobal
	?*dir* = up
)
	
(deftemplate tile
	(slot x)
	(slot y)
	(slot direction)
	(slot type (default current))
	(slot fieldType (default NORMAL))) 
	
(deffacts startup
	(bot)
	(tile))
	
;(defrule goUp
;	(bot (hitPoints ?hp) (actionPoints ?ap))
;	(test (> ?ap 0))
;	=>
;	(bind ?*dir* up))

(defrule goFood
	(bot (hitPoints ?hp) (actionPoints ?ap))
	(tile (direction ?d) (type neighbour) (fieldType ?t))
	(test (and (>= ?ap 3) (eq ?t FOOD)))
	=>
	(printout t ?ap crlf)
	(bind ?*dir* ?d))