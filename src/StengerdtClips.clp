(deftemplate bot
	(slot HP (default 15) (type INTEGER))
	(slot PP (default 15) (type INTEGER))
	(slot AP (default 5) (type INTEGER))
	(slot WP (default 0) (type INTEGER))
	(slot state (default current))
	(slot posX (default -1) (type INTEGER))
	(slot posY (default -1) (type INTEGER))
)

(deftemplate tile
	(slot x (type INTEGER))
	(slot y (type INTEGER))
	(slot type (default current))
	(slot fieldType (default NORMAL))
) 

(deftemplate myTrapPosition
	(slot x (type INTEGER))
	(slot y (type INTEGER))
)

;POSSIBLE ACTIONS:
;up
;down
;left
;right
;throw
;fire
;trap
;nothing
(defglobal
	?*action* = random
)

(defglobal
	?*curPrio* = 0
)

(defglobal
	?*throwXCoord* = 1
)
(defglobal
	?*throwYCoord* = 1
)

;SOME CONST TO PRIORITIZE ACTIONS
(defglobal
	?*FOOD_PRIORITY* = 30
)
(defglobal
	?*MOVE_PRIORITY* = 5
)
(defglobal
	?*ATTACK_PRIORITY* = 10
)
(defglobal
	?*WOOD_PRIORITY* = 20
)
(defglobal
	?*FIRE_PRIORITY* = 25
)

;FUNCTIONS
(deffunction findDirection
	(?bx ?by ?tx ?ty ?priority)
	
	(if (> ?bx ?tx)
		then
		(bind ?*action* left)
		(bind ?*curPrio* ?priority)
	else
		(if (< ?bx ?tx)
			then
			(bind ?*action* right)
			(bind ?*curPrio* ?priority)
		else
			(if (< ?by ?ty)
				then
				(bind ?*action* up)
				(bind ?*curPrio* ?priority)
			else
				(if (> ?by ?ty)
					then
					(bind ?*action* down)
					(bind ?*curPrio* ?priority)
				else
					(bind ?*action* nothing)
					(bind ?*curPrio* 0)
				)
			)
		)
	)
)

(defrule searchForFood
	(bot  (HP ?hp) (PP ?pp) (AP ?ap) (WP ?wp) (state current) (posX ?bx) (posY ?by))
	(tile (x ?fx) (y ?fy) (type ?t) (fieldType ?ft))
	=>
	(if (= ?*curPrio* 0)
		then
		(if (> ?hp 10)
			then
			(bind ?*action* nothing)
			(bind ?*curPrio* 0)
		else
			(if (not(eq ?ft FOOD))
			then
				(bind ?*action* nothing)
				(bind ?*curPrio* 0)
			else
				(if (>= ?ap 3)
					then
					(findDirection ?bx ?by ?fx ?fy ?*FOOD_PRIORITY*)
				else
					(bind ?*action* nothing)	
					(bind ?*curPrio* 0)
				)
			)
		)
	)
)

(defrule kindleFire
	(bot (HP ?hp) (PP ?pp) (AP ?ap) (WP ?wp))
	=>
	(if (= ?*curPrio* 0)
		then
		(if (> ?ap 3)
			then
			(if (> ?wp 1)
			then
				(if (< ?pp 5)
				then
					(bind ?*action* fire)
					(bind ?*curPrio* ?*FIRE_PRIORITY*)
				else
					(bind ?*action* nothing)
					(bind ?*curPrio* 0)
				)
			)
		)
	)
)

(defrule searchForWood
	(bot  (HP ?hp) (PP ?pp) (AP ?ap) (WP ?wp) (state current) (posX ?bx) (posY ?by))
	(tile (x ?wx) (y ?wy) (type ?t) (fieldType ?ft))
	=>
	(if (= ?*curPrio* 0)
		then
		(if (> ?wp 3)
			then
			(bind ?*action* nothing)
			(bind ?*curPrio* 0)
		else
			(if (not(eq ?ft WOOD))
			then
				(bind ?*action* nothing)
				(bind ?*curPrio* 0)
			else
				(if (>= ?ap 3)
					then
					(findDirection ?bx ?by ?wx ?wy ?*WOOD_PRIORITY*)
				else
					(bind ?*action* nothing)	
					(bind ?*curPrio* 0)
				)
			)
		)
	)
)