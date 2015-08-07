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
;food
;wood
;fire
;trap
;nothing
(defglobal
	?*action* = nothing
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

(defglobal
	?*foodX* = -1
)

(defglobal
	?*foodY* = -1
)

(defglobal
	?*woodX* = -1
)

(defglobal
	?*woodY* = -1
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

(defrule searchFood
	(bot (HP ?hp) (AP ?ap) (PP ?pp))
	(tile (x ?fx) (y ?fy) (type ?t) (fieldType ?ft))
	=>
	(if (= ?*curPrio* 0)
		then
		(if (and (<= ?hp ?pp) (< ?hp 8))
			then
			(if (eq ?ft FOOD)
				then
				(if (>= ?ap 3)
					then
					(bind ?*action* food)
					(bind ?*foodX* ?fx)
					(bind ?*foodY* ?fy)
					(bind ?*curPrio* ?*FOOD_PRIORITY*)
				)
			)
		)
	)
)

(defrule doFire
	(bot (PP ?pp) (WP ?wp) (AP ?ap))
	=>
	(if (= ?*curPrio* 0)
		then
		(if (< ?pp 7)
			then
			(if (>= ?ap 3)
				then
				(if (> ?wp 0)
					then
					(bind ?*action* fire)
					(bind ?*curPrio* ?*FIRE_PRIORITY*)
				)
			)
		)
	)
)

(defrule searchWood
	(bot (WP ?wp) (AP ?ap))
	(tile (x ?wx) (y ?wy) (type ?t) (fieldType ?ft))
	=>
	(if (eq ?ft WOOD)
		then
		(if (= ?*curPrio* 0)
			then
			(if (<= ?wp 2)
				then
				(if (>= ?ap 3)
					then
					(bind ?*action* wood)
					(bind ?*curPrio* ?*WOOD_PRIORITY*)
					(bind ?*woodX* ?wx)
					(bind ?*woodY* ?wy)
				)
			)
		)
	)
)