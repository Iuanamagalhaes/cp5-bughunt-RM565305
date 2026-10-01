# Checkpoint 5 — Bug Hunt PetFiap

## Identificação

**Grupo:** Feito individual

| Integrante | RM | Turma |
|---|---|---|
| Luana Magalhães Freire| 565305 | 2CCPH |

| Campo | |
|---|---|
| **Total de bugs corrigidos** | 12 / 12 |
| **Total de ajustes de Clean Code** | 6 / 6 |
| **Total de testes novos escritos** | 6 / 6 |
| **Suíte final (Run As → JUnit Test)** | 26 testes, 0 falhas |

---

## Parte 1 — Bugs encontrados

| # | Sintoma observado (o que fiz/vi) | Causa raiz (arquivo e linha aproximada) | Correção aplicada | Conceito da disciplina |
|---|---|---|---|---|
| bug01 | `GeradorProtocoloTest.deveManterUmaUnicaInstancia` e `deveGerarProtocolosSequenciais` falhavam — cada chamada devolvia objeto novo com contador zerado | `GeradorProtocolo.java`, método `getInstancia()` (~linha 15): `if (instancia == null) { return new GeradorProtocolo(); }` nunca atribuía à variável estática | Trocado `return new GeradorProtocolo()` por `instancia = new GeradorProtocolo();` antes do `return instancia` | Padrão Singleton (Aula 14) |
| bug02 | `AtendimentoBuilderTest.deveMontarAtendimentoCompleto` falhava com `expected: <Rex> but was: <null>` | `AtendimentoBuilder.java`, método `comPet()` (~linha 18): `petNome = petNome` (shadowing do parâmetro) em vez de `this.petNome = petNome` | Adicionado `this.` na atribuição de `petNome` | Encapsulamento / atributos de instância vs parâmetros locais (Aula 7) |
| bug03 | `deveRecusarMontagemSemNomeDoPet` e `deveRecusarMontagemSemPorte` falhavam — nenhuma exceção era lançada | `AtendimentoBuilder.java`, método `construir()` (~linha 32): delegava direto pra Factory sem validar campos obrigatórios | Adicionado `if (petNome == null \|\| petPorte == null) throw new IllegalArgumentException(...)` antes de chamar a Factory | Validação de invariantes / padrão Builder (Aula 14) |
| bug04 | `AtendimentoFactoryTest.deveCriarTosaQuandoTipoForTosa` falhava: pedia Tosa e recebia Banho | `AtendimentoFactory.java`, `switch` do método `criar()` (~linha 14): `case "TOSA" -> new Banho(...)` | Corrigido `case "TOSA"` para instanciar `Tosa` | Padrão Factory (Aula 14) |
| bug05 | `devePreencherOsDadosDoPetNaConsulta` falhava: todos os campos vinham nulos/zero | `ConsultaVeterinaria.java`, construtor (~linha 14): chamava `super()` vazio em vez de repassar os parâmetros | Trocado `super()` por `super(protocolo, petNome, petPorte, tutorNome, dataHora)` | Herança / encadeamento de construtores (Aula 7) |
| bug06 | `deveRecusarAgendamentoComHorarioJaOcupado` falhava: conflito de horário não era detectado | `AgendaService.java`, método `agendar()` (~linha 20): `a.getPetNome() == novo.getPetNome()` e `a.getDataHora() == novo.getDataHora()` (comparação por referência) | Trocado `==` por `.equals()` nas duas comparações | `==` vs `.equals()` em objetos (Aula 7) |
| bug07 | `deveLancarExcecaoQuandoAtendimentoNaoExiste` falhava: exceção não chegava a quem chamou | `AgendaService.java`, método `buscarPorId()` (~linha 32): `catch (Exception e) { return null; }` capturava a própria exceção recém-lançada | Removido o `try/catch` genérico; método deixa a exceção propagar | Tratamento de exceções — catch genérico esconde erros (Aula 11) |
| bug08 | Nenhum teste vermelho apontava direto; ao comparar `Banho.calcularPreco()` com a tabela da Parte C, PEQUENO e GRANDE estavam trocados | `Banho.java`, método `calcularPreco()` (~linha 22): `PEQUENO → 100.0` e `GRANDE → 60.0` (invertidos) | Invertidos os retornos: PEQUENO → 60.0, GRANDE → 100.0 | Regras de negócio no model / polimorfismo (Aula 14) |
| bug09 | Nenhum teste vermelho apontava; ao ler `Tosa.java` havia `getDuracaoMinutos(String porte)` nunca chamado por ninguém | `Tosa.java`, método `getDuracaoMinutos(String porte)` (~linha 38): assinatura diferente da classe-mãe, não sobrescrevia | Removido o parâmetro, adicionado `@Override`, mantido retorno `60` | Sobrescrita (override) vs sobrecarga (overload) (Aula 7) |
| bug10 | Nenhum teste cobria; tabela de "Regras de status" exige recusa ao cancelar atendimento CONCLUIDO/CANCELADO | `Atendimento.java`, método `cancelar()` (~linha 52): `status = "CANCELADO"` incondicional, sem checar status atual | Adicionado `if (!"AGENDADO".equals(status)) throw new StatusInvalidoException(...)` antes de trocar o status | Máquina de estados / invariantes de domínio (Aula 14) |
| bug11 | Nenhum teste cobria; tabela de "Regras de agendamento" exige recusa com `IllegalArgumentException` para data no passado, sem consultar o banco | `AgendaService.java`, método `agendar()` (~linha 17): não validava `novo.getDataHora()` antes de consultar o repositório | Adicionado `if (novo.getDataHora().isBefore(LocalDateTime.now())) throw new IllegalArgumentException(...)` no início do método | Validação de pré-condições / fail-fast (Aula 11) |
| bug12 | Encontrado só em code review: nenhum teste roda com banco real (Mockito substitui o repository) | `Atendimento.java`, campo `id` (~linha 13): anotado só com `@Id`, sem `@GeneratedValue` | Adicionado `@GeneratedValue(strategy = GenerationType.IDENTITY)` no campo `id` | JPA / mapeamento objeto-relacional (Aula 12/13) |

## Parte 2 — Ajustes de Clean Code

| # | Onde estava | Qual princípio/boas práticas era violado | O que eu mudei |
|---|---|---|---|
| clean01 | Construtor privado de `GeradorProtocolo.java` | `System.out.println` de debug esquecido dentro de código de produção | Removido o `System.out.println("GeradorProtocolo criado!")` |
| clean02 | `AtendimentoBuilder.construir()` | Comentário desatualizado ("validação fica por conta do controller") contradizia o contrato e ajudava a esconder o bug03 | Reescrito o comentário para refletir que a validação acontece no próprio `construir()` |
| clean03 | `AtendimentoFactory.criar(int p, String t, String n, String po, String tu, LocalDateTime d)` | Nomes de parâmetro de uma letra, sem significado — dificulta leitura e manutenção | Parâmetros renomeados para `protocolo, tipo, petNome, petPorte, tutorNome, dataHora` |
| clean04 | `AgendaService.agendar()` | `System.out.println` dentro da camada de serviço, misturando regra de negócio com saída para console, sem nenhum valor de diagnóstico real | Removido o `System.out.println` do "recibo" |
| clean05 | `AtendimentoController.calcularDescontoFidelidade(int pontos)` | Código morto: método privado nunca chamado em lugar nenhum do sistema, implementando regra que nem foi aprovada ("Fidelidade (futuro)") | Método removido do controller |
| clean06 | `GeradorProtocolo.getInstancia()` | Comentário da classe afirmava "thread-safe", mas o método não tinha nenhuma sincronização — comentário promete o que o código não cumpre | Adicionado `synchronized` ao método `getInstancia()` |

## Parte 3 — Testes novos (regras que estavam sem cobertura)

| # | Teste escrito (classe.método) | Regra coberta | Resultado ao escrever (vermelho/verde) |
|---|---|---|---|
| teste01 | `BanhoTest.deveCustar60ReaisParaPortePequeno` / `deveCustar100ReaisParaPorteGrande` | Preço do Banho por porte (R$60/80/100) | Vermelho — revelou o bug08 (preço pequeno/grande invertido) |
| teste02 | `ConsultaVeterinariaTest.deveCustar150ReaisIndependenteDoPorte` | Preço fixo de R$150 na consulta, independente do porte | Verde — regra já estava correta |
| teste03 | `TosaTest.deveDurar60Minutos` | Duração da Tosa (60 min) | Vermelho — revelou o bug09 (overload em vez de override) |
| teste04 | `AgendaServiceTest.deveRecusarAgendamentoComDataNoPassado` | Agendar com data/hora no passado → `IllegalArgumentException`, banco não é consultado | Vermelho — revelou o bug11 (validação inexistente) |
| teste05 | `AgendaServiceTest.deveRecusarCancelamentoDeAtendimentoJaConcluido` | Cancelar um atendimento já CONCLUIDO deve recusar | Vermelho — revelou o bug10 (`cancelar()` sem validação de status) |
| teste06 | `AgendaServiceTest.deveRecusarConclusaoDeAtendimentoJaCancelado` | Concluir um atendimento já CANCELADO deve recusar | Verde — regra já estava correta |

---

## Parte 4 — Perguntas de reflexão
> Responda com suas palavras, 5 a 10 linhas cada, **usando o código real do
> projeto como exemplo**. Respostas genéricas de tutorial não pontuam.

### 1. A suíte como contrato (Aula 15)

**O projeto chegou com 20 testes, 9 vermelhos. Descreva como você usou as mensagens de falha (ex.: `expected: <Rex> but was: <null>`) para caçar os bugs. O que a suíte de testes tem de melhor do que testar tudo na mão com curl?**

Quando rodei a suíte pela primeira vez no Eclipse, apareceram 9 testes vermelhos entre os 20 existentes. As mensagens do JUnit ajudaram bastante a localizar os problemas, porque mostravam o que era esperado e o que realmente estava acontecendo. No `AtendimentoBuilderTest.deveMontarAtendimentoCompleto`, por exemplo, apareceu `expected: <Rex> but was: <null>`, o que me levou direto ao método `comPet()` do `AtendimentoBuilder`. Lá estava `petNome = petNome` em vez de `this.petNome = petNome`, então o atributo não era preenchido (bug02). No `GeradorProtocoloTest.deveManterUmaUnicaInstancia`, o `assertSame(primeira, segunda)` mostrou que o Singleton estava criando objetos diferentes (bug01). A suíte também é melhor que testar tudo manualmente com `curl` porque os cenários já ficam registrados, podem ser executados várias vezes e não dependem de subir a aplicação ou acessar o banco.

### 2. Mock e injeção de dependência (Aulas 13 a 15)

**No `AgendaServiceTest`, o `@Mock` cria um `AtendimentoRepository` falso e o `@InjectMocks` o injeta no service. Explique a relação disso com o `@Autowired` que o Spring faz em produção — quem "injeta" em cada mundo, e por que o teste consegue rodar sem banco e sem subir o Spring?**

No `AgendaServiceTest`, o `@Mock` cria um `AtendimentoRepository` falso, enquanto o `@InjectMocks` coloca esse mock dentro do `AgendaService`. Na aplicação rodando normalmente, quem faz essa injeção é o Spring através do `@Autowired`, entregando o repository real que conversa com o banco. No teste, quem faz esse trabalho é o Mockito, usando os comportamentos definidos nos `when(...).thenReturn(...)`. Por isso o `AgendaService` consegue ser testado sem precisar iniciar o Spring ou acessar o Oracle. O service recebe uma dependência falsa, mas consegue executar sua lógica normalmente. Isso deixa o teste mais rápido e também evita que um problema de conexão com o banco atrapalhe a execução.

### 3. `==` vs `.equals()` (Aula 7)

**Um dos bugs fazia o agendamento duplicado passar pela verificação de conflito. Explique por que `==` entre Strings e `LocalDateTime` falhou aqui, por que ele "funciona por sorte" com literais como `"Rex"`, e o que a sua correção mudou.**

No `AgendaService.agendar()`, a verificação de conflito usava `a.getPetNome() == novo.getPetNome()` e `a.getDataHora() == novo.getDataHora()`. O problema é que, para objetos, `==` compara a referência na memória e não o conteúdo. Com `"Rex"`, isso podia parecer funcionar por causa do *string pool* do Java, mas não era uma comparação segura. O teste `deveRecusarAgendamentoComHorarioJaOcupado` mostrou o problema porque criava um novo `LocalDateTime` com o mesmo valor do agendamento existente. Mesmo tendo a mesma data e hora, eram objetos diferentes, então o `==` retornava `false`. A correção foi trocar as comparações por `.equals()`, fazendo a comparação pelo conteúdo dos objetos.

### 4. Sobrescrita vs sobrecarga (Aula 7)

**Um dos bugs compilava sem nenhum erro: um método parecia sobrescrever `getDuracaoMinutos`, mas na verdade criava uma assinatura nova. Explique a diferença entre override e overload nesse caso e por que a anotação `@Override` teria impedido o bug.**

No `Tosa.java`, existia um método `getDuracaoMinutos(String porte)`, mas na classe `Atendimento` o método era `getDuracaoMinutos()` sem parâmetros. Por causa dessa diferença, o método da `Tosa` era uma sobrecarga e não uma sobrescrita. O código compilava normalmente, mas quando o sistema chamava `tosaDoRex().getDuracaoMinutos()`, acabava usando o método da classe `Atendimento`, que retornava `30`, quando a regra da Tosa deveria retornar `60`. Como o método estava com uma assinatura diferente, o compilador não tinha motivo para considerar aquilo um erro. Se tivesse sido usado `@Override`, o compilador teria identificado imediatamente que aquele método não estava sobrescrevendo nenhum método da classe-pai.

### 5. Singleton manual vs bean do Spring (Aula 14)

**O `GeradorProtocolo` é um Singleton escrito à mão e causou um dos bugs. Explique o que ele garante, qual foi o bug, e por que o `AgendaService` (`@Service`) não corre o mesmo risco no container do Spring.**

O `GeradorProtocolo` foi criado como um Singleton manual para garantir uma única instância durante a execução da aplicação e manter um contador único para os protocolos. O bug estava no `getInstancia()`: quando `instancia` era `null`, o código fazia `return new GeradorProtocolo()` sem salvar o objeto na variável `instancia`. Assim, uma nova instância podia ser criada a cada chamada e o contador acabava sendo reiniciado. Já o `AgendaService`, por possuir `@Service`, tem seu ciclo de vida controlado pelo container do Spring. Por padrão, o Spring mantém uma única instância desse bean dentro do contexto da aplicação. Nesse caso, não precisamos implementar manualmente a lógica de Singleton, porque o próprio framework controla a criação e o compartilhamento do objeto.

### 6. Cobertura de testes: onde parar? (Aula 15)

**Dos 6 testes novos que você escreveu, alguns ficaram vermelhos (revelaram bugs) e outros verdes de cara (regras já corretas). Vale a pena manter os que ficaram verdes? Em um projeto real com prazo, o que você priorizaria testar: caminho feliz, caminhos de erro, ou 100% de cobertura? Justifique.**

Dos 6 testes novos, o `teste02` (`ConsultaVeterinariaTest.deveCustar150ReaisIndependenteDoPorte`) e o `teste06` (`AgendaServiceTest.deveRecusarConclusaoDeAtendimentoJaCancelado`) já ficaram verdes na primeira execução porque essas regras estavam corretas. Mesmo assim, vale manter os dois porque eles servem como proteção contra alterações futuras. Se alguém mudar, por exemplo, o `calcularPreco()` da `ConsultaVeterinaria`, o teste pode mostrar imediatamente que a regra deixou de funcionar. Em um projeto real com prazo curto, eu priorizaria primeiro os cenários principais e depois os caminhos de erro das regras de negócio, como conflito de horário, dados obrigatórios e status inválidos. Não colocaria 100% de cobertura como prioridade, porque ter uma porcentagem alta não significa necessariamente que os comportamentos importantes estejam bem testados. O mais importante é testar as regras que realmente podem causar problemas no funcionamento do sistema.
