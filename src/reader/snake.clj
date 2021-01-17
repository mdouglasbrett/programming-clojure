; START: namespace
(ns reader.snake
  (:import (java.awt Color Dimension)
           (javax.swing JPanel JFrame Timer JOptionPane)
           (java.awt.event ActionListener KeyListener))
  (:use examples.import-static))

(import-static java.awt.event.KeyEvent VK_LEFT VK_RIGHT VK_UP VK_DOWN)

; END: namespace

(def width 75)
(def height 50)
(def point-size 10)
(def turn-millis 75)
(def win-length 5)
(def dirs
  {VK_LEFT  [-1 0]
   VK_RIGHT [1 0]
   VK_UP    [0 -1]
   VK_DOWN  [0 1]})

(defn add-points [& pts]
  (vec (apply map + pts)))

(defn point-to-screen-rect [pt]
  (map #(* point-size %)
       [(pt 0) (pt 1) 1 1]))

(defn create-apple []
  {:location [(rand-int width) (rand-int height)]
   :color    (Color. 210 50 90)
   :type     :apple})

(defn create-snake []
  {:body  (list [1 1])
   :dir   [1 0]
   :type  :snake
   :color (Color. 15 160 70)})

(defn move [{:keys [body dir] :as snake} & grow]
  (assoc snake :body
         (cons (add-points (first body) dir)
               (if grow body (butlast body)))))