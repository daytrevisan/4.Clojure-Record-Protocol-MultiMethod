(ns hospital.aula1
  (:use clojure.pprint))

; Mapa 'pacientes' { 15 {paciente 15}, 23 {paciente 23} }
(defn adiciona-paciente
  [pacientes paciente]
  (if-let [id (:id paciente)]
    (assoc pacientes id paciente)
    (throw (ex-info "Paciente não possui id" {:paciente paciente}))))

; 'if-let' substitui o seguinte código
;(let [id (:id paciente)]
;  (if id
;    (assoc pacientes id paciente)
;    (throw (ex-info "Paciente não possui id" {:paciente paciente}))))

(defn testa-uso-de-pacientes []
  (let [pacientes {}
        guilherme {:id 15, :nome "Guilherme", :nascimento "18/09/1981"}
        debora {:id 20, :nome "Debora", :nascimento "18/09/1982"}
        paulo {:nome "Paulo", :nascimento "18/10/1983"}
        ]
    (pprint (adiciona-paciente pacientes guilherme))
    (pprint (adiciona-paciente pacientes debora))
    (pprint (adiciona-paciente pacientes paulo))
    ))

;(testa-uso-de-pacientes)

; DEFRECORD >>> trabalha de maneira similar a um -hashmap- (registro)
(defrecord Paciente [id, nome, nascimento])

; 'Paciente' na prática será uma classe (de Java)
(println (->Paciente 15 "Guilherme" "18/09/1981"))
(pprint (->Paciente 15 "Guilherme" "18/09/1981"))

; O ponto (.) chama o construtor
; O acento circunflexo (ˆ) passa uma dica (indica o tipo de dado) -> pode-se estabelecer como -int, long, string- etc
; Mesmo resultado do que quando chama com '->Paciente'
; Mesmo passando parâmetros fora de ordem, será agregado conforme a construção de -defrecord-
(pprint (Paciente. 15 "Guilherme" "18/09/1981"))
(pprint (Paciente. "Guilherme" 15 "18/09/1981"))
(pprint (Paciente. 15  "18/09/1981" "Guilherme"))

; Definindo explicitamente o que é o mapa (passando pro paciente um mapa -> parâmetros como keywords)
(pprint (map->Paciente {:id 15, :nome "Guilherme", :nascimento "18/09/1981"}))

(let [guilherme (->Paciente 15 "Guilherme" "18/09/1981")]
  (println (:id guilherme))
  (println (vals guilherme))
  (println (class guilherme))
  (println (record? guilherme))                             ; pergunta se é um defrecord
  (println (.id guilherme)))                                ; acesso como se fosse um objeto em Java

; Construção com -map- >>> parâmetros opcionais (nem todos precisam ser passados)
; Permite construir sem -id-
(pprint (map->Paciente {:id 15, :nome "Guilherme", :nascimento "18/09/1981", :rg "22222222"}))
(pprint (map->Paciente {:nome "Guilherme", :nascimento "18/09/1981", :rg "22222222"}))

; Construção com construtor >>> parâmetros obrigatórios (todos devem ser passados)
;(pprint (Paciente. "Guilherme" "18/09/1981"))

; ASSOC -> "transformou" a classe em mapa
(pprint (assoc (Paciente. nil "Guilherme" "18/09/1981") :id 38))

(pprint (class (assoc (Paciente. nil "Guilherme" "18/09/1981") :id 38))) ; mostra a classe 'Paciente'

; Verificando se são os valores são exatamente os mesmos
(pprint (= (->Paciente 15 "Guilherme" "18/09/1981") (->Paciente 15 "Guilherme" "18/09/1981"))) ; true
(pprint (= (->Paciente 153 "Guilherme" "18/09/1981") (->Paciente 15 "Guilherme" "18/09/1981"))) ; false

; A decisão de usar um Protocol ou um mapa pode estar relacionada a -performance- ou
; a questão de -modelar aquela parte da aplicação com OO-

; É possível implementar ciclo de vida, estado e encapsulamento em Clojure sem utilizar inter operabilidade com Java.
; Portanto é uma escolha ir por componentes OO ou funcionais.