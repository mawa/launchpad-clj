;; Launchpad S and Launchpad Mini

(ns launchpad.core
  (:require midi))

(declare reset)

(defn get-launchpad
  "Find and initialize the first connected Launchpad"
  []
  (let [lp (midi/get-receiver "Launchpad")]
    (do (reset lp) lp)))

(defn mk-color
  "Make a color. red and green range from 0 to 3"
  [r g]
  (bit-or
   r
   0xC
   (bit-shift-left g 4)))

(defn light
  "light a pad at x,y (0,0 is top-left). Use mk-color for c"
  [m x y c]
  (midi/send m (midi/note-on
                 (+ x (* 0x10 y))
                 c)))

(defn unlight
  "unlight a pad"
  [m x y]
  (light m x y 0))

(defn reset
  "reset the launchpad"
  [m] (midi/send m (midi/control-change 0 0)))

(defn all-on
  "brightness is 1, 2 or 3"
  [m brightness]
  (midi/send m (midi/control-change 0 (+ 124 brightness))))

;; afaict this should work but I don't see any sysex sent with midi monitor,
;; so it might be an overtone bug.
;(defn text
;  [m color byte-seq]
;  (midi/midi-sysex m [240 0 32 41 9 color byte-seq 0xf7]))

;(defn clear-text
;  "If text is looping (add 64 to color to loop), reset it"
;  [m]
;  (midi/midi-sysex m [240, 0, 32, 41, 9, 0, 247]))

(defn top-button
  "Light button 0-7"
  [m n col]
  (midi/send m (midi/control-change (+ 0x68 n) col)))

(defn right-button
  "Light right button 0-7"
  [m n col]
  (light m 8 n col))

;; That should do it. There's more about intensity (wishlist) and about
;; double buffering and other tricks for more efficient updates, which I
;; may utilize when doing react-style

;; well actually I should do input handling too. but in the context of
;; the richer api, because we have to at least keep track of the event
;; handlers.
