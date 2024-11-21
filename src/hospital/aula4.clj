(ns hospital.aula4
  (:use [clojure.pprint]))

(defrecord PacienteParticular [id, nome, nascimento, situacao])
(defrecord PacientePlanoDeSaude [id, nome, nascimento, situacao, plano])

; deve-assinar-pre-autorizacao?
; >= 50; plano

(defprotocol Cobravel
  (deve-assinar-pre-autorizacao? [paciente procedimento valor]))

(defn nao-eh-urgente? [paciente]
  (not= :urgente (:situacao paciente :normal)))

(extend-type PacienteParticular
  Cobravel
  (deve-assinar-pre-autorizacao? [paciente, procedimento, valor]
    (and (>= valor 50) (nao-eh-urgente? paciente))))

(extend-type PacientePlanoDeSaude
  Cobravel
  (deve-assinar-pre-autorizacao? [paciente, procedimento, valor]
    (let [plano (:plano paciente)]
      (and (not (some #(= % procedimento) plano)) (nao-eh-urgente? paciente)))))

(let [particular (->PacienteParticular 15, "Guilherme", "18/09/1981", :normal)
      plano (->PacientePlanoDeSaude 16, "Lucia", "24/10/1991", :normal [:raio-x, :ultrassom])]
  (pprint (deve-assinar-pre-autorizacao? particular, :raio-x, 500))
  (pprint (deve-assinar-pre-autorizacao? particular, :raio-x, 40))
  (pprint (deve-assinar-pre-autorizacao? plano, :raio-x, 499990))
  (pprint (deve-assinar-pre-autorizacao? plano, :coleta-de-sangue, 499990))
  (pprint (deve-assinar-pre-autorizacao? plano, :ultrassom, 499990))
  )

(let [particular (->PacienteParticular 15, "Guilherme", "18/09/1981", :urgente)
      plano (->PacientePlanoDeSaude 16, "Lucia", "24/10/1991", :urgente [:raio-x, :ultrassom])]
  (pprint (deve-assinar-pre-autorizacao? particular, :raio-x, 500))
  (pprint (deve-assinar-pre-autorizacao? particular, :raio-x, 40))
  (pprint (deve-assinar-pre-autorizacao? plano, :raio-x, 499990))
  (pprint (deve-assinar-pre-autorizacao? plano, :coleta-de-sangue, 499990))
  (pprint (deve-assinar-pre-autorizacao? plano, :ultrassom, 499990))
  )

; Colocar 'multi' no final não é padrão, mas por estar no mesmo arquivo, diferenciamos as funções
(defmulti deve-assinar-pre-autorizacao-multi? class)

(defmethod deve-assinar-pre-autorizacao-multi? PacienteParticular [paciente]
  (pprint "invocando paciente particular")
  true)

(defmethod deve-assinar-pre-autorizacao-multi? PacientePlanoDeSaude [paciente]
  (pprint "invocando paciente plano de saúde")
  false)

(let [particular (->PacienteParticular 15, "Guilherme", "18/09/1981", :urgente)
      plano (->PacientePlanoDeSaude 16, "Lucia", "24/10/1991", :urgente [:raio-x, :ultrassom])]
  (pprint (deve-assinar-pre-autorizacao-multi? particular))
  (pprint (deve-assinar-pre-autorizacao-multi? plano)))

; Explorando como funciona a função que define a estratégia de um defmulti
(defn minha-funcao [p]
  (println p)
  (class p))
; 'class' transforma o parâmetro em uma classe

;(defmulti multi-teste minha-funcao)
;(multi-teste "guilherme")
;(multi-teste :guilherme)




; pedido { :paciente paciente, :valor valor, :procedimento procedimento }

; Estamos misturando keyword e classe como chave
(defn tipo-de-autorizador [pedido]
  (let [paciente (:paciente pedido)
        situacao (:situacao paciente)
        urgencia? (= :urgente situacao)]
    (if urgencia?
      :sempre-autorizado
      (class paciente))))

(defmulti deve-assinar-pre-autorizacao-do-pedido? tipo-de-autorizador)

(defmethod deve-assinar-pre-autorizacao-do-pedido? :sempre-autorizado [pedido]
  false)

(defmethod deve-assinar-pre-autorizacao-do-pedido? PacienteParticular [pedido]
  (>= (:valor pedido 0) 50))

(defmethod deve-assinar-pre-autorizacao-do-pedido? PacientePlanoDeSaude [pedido]
  (not (some #(= % (:procedimento pedido)) (:plano (:paciente pedido)))))

(let [particular (->PacienteParticular 15, "Guilherme", "18/09/1981", :urgente)
      plano (->PacientePlanoDeSaude 16, "Lucia", "24/10/1991", :urgente [:raio-x, :ultrassom])]
  (pprint (deve-assinar-pre-autorizacao-multi? {:paciente particular, :valor 1000, :procedimento :coleta-de-sangue}))
  (pprint (deve-assinar-pre-autorizacao-multi? {:paciente plano, :valor 1000, :procedimento :coleta-de-sangue})))

(let [particular (->PacienteParticular 15, "Guilherme", "18/09/1981", :normal)
      plano (->PacientePlanoDeSaude 16, "Lucia", "24/10/1991", :normal [:raio-x, :ultrassom])]
  (pprint (deve-assinar-pre-autorizacao-multi? {:paciente particular, :valor 1000, :procedimento :coleta-de-sangue}))
  (pprint (deve-assinar-pre-autorizacao-multi? {:paciente plano, :valor 1000, :procedimento :coleta-de-sangue})))

; Multi methods podem espalhar as implementações em vários namespaces, Protocols também.
; Portanto devemos fazer a escolha por causa de interoperabilidade com Java ou outros fatores.