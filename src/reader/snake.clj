; START: namespace
(ns reader.snake
  (:import (java.awt Color Dimension)
           (javax.swing JPanel JFrame Timer JOptionPane)
           (java.awt.event ActionListener KeyListener))
  (:use examples.import-static))

(import-static java.awt.event.KeyEvent VK_LEFT VK_RIGHT VK_UP VK_DOWN)

; END: namespace


(def test-apple (ref nil))
(def test-snake (ref nil))

;; Game constants

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

;; Functional model

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
         ;; The cons is operating on the result of the add-points and if
         (cons (add-points (first body) dir)
               (if grow body (butlast body)))))

(defn win? [{body :body}]
  (>= (count body) win-length))

(defn head-overlaps-body? [{[head & body] :body}]
  (contains? (set body) head))

(def lose? head-overlaps-body?)

(defn eats? [{[snake-head] :body} {apple :location}]
  (= snake-head apple))

(defn turn [snake newDir]
  (assoc snake :dir newDir))

;; Mutations

(defn reset-game [snake apple]
  (dosync (ref-set apple (create-apple))
          (ref-set snake (create-snake)))
  nil)

(defn update-direction [snake newDir]
  (when newDir (dosync (alter snake turn newDir))))

(defn update-positions [snake apple]
  (dosync
   (if (eats? @snake @apple)
     (do (ref-set apple (create-apple))
       (alter snake move :grow))
     (alter snake move)))
  nil)

;; GUI

(defn fill-point [g pt color]
  (let [[x y width height] (point-to-screen-rect pt)]
    (.setColor g color)
    (.fillRect g x y width height)))

(defmulti paint (fn [g object & _] (:type object)))

(defmethod paint :apple
  [g {:keys [location color]}]
  (fill-point g location color))
(defmethod paint :snake
  [g {:keys [body color]}]
  (doseq [point body]
    (fill-point g point color)))

(defn game-panel [frame snake apple]
  (proxy [JPanel ActionListener KeyListener] []
         (paintComponent [g]
                         (proxy-super paintComponent g)
                         (paint g @snake)
                         (paint g @apple))
         (actionPerformed [e]
                          (update-positions snake apple)
                          (when (lose? @snake)
                            (reset-game snake apple)
                            (JOptionPane/showMessageDialog frame "You lose!"))
                          (when (win? @snake)
                            (reset-game snake apple)
                            (JOptionPane/showMessageDialog frame "You win!"))
                          (. repaint this))
         (keyPressed [e]
                     (update-direction snake (dirs (. getKeyCode e))))
         (getPreferredSize []
                           (Dimension. (* (inc width) point-size)
                                       (* (inc height) point-size)))
         (keyReleased [e])
         (keyTyped [e])))

