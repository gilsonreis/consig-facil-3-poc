# Diretrizes de Design e Arquitetura Frontend - Portal do Servidor

## 1. Stack Tecnológico e Padrões
* **Framework:** Angular 19 (Strictly Standalone Components).
* **Estilização:** Tailwind CSS (Mobile-first).
* **Ícones:** Lucide Icons ou Heroicons (padrão SVGs limpos) ou Bootstrap Icons.
* **Control Flow:** Utilizar a sintaxe moderna do Angular (`@if`, `@for`, `@switch`) em vez de diretivas estruturais (`*ngIf`, `*ngFor`).

## 2. Identidade Visual (Tailwind UI)
O sistema deve transmitir confiança, clareza financeira e modernidade.
* **Cores Principais (Primary):** Tons de Azul para ações principais (ex: `bg-blue-600`, `hover:bg-blue-700`).
* **Cores de Sucesso (Financeiro):** Tons de Esmeralda para margem e valores positivos (ex: `text-emerald-600`, `bg-emerald-50`).
* **Cores de Alerta/Perigo:** Tons de Vermelho e Âmbar para bloqueios ou margem excedida (ex: `text-red-600`, `bg-red-50`).
* **Backgrounds:** Fundo da aplicação em `bg-slate-50` para destacar os cards brancos (`bg-white`).
* **Cards e Containers:** Utilizar bordas arredondadas e sombras suaves (`rounded-2xl`, `shadow-sm` ou `shadow-md`, `border border-slate-200`).
* **Tipografia:** Fonte limpa (Inter ou Roboto). Textos de apoio em `text-slate-500`, títulos em `text-slate-800 font-bold`.

## 3. Estado Global e Regra de Negócio Central (Identidade vs. Papel)
* **Usuário vs. Matrícula:** Um usuário logado (Pessoa Física) pode possuir múltiplas matrículas. 
* **Seletor de Contexto:** A aplicação DEVE possuir um estado global (Service com `Signal` ou `BehaviorSubject`) chamado `MatriculaAtiva`. 
* **Reatividade:** Toda a interface (Dashboard, Simulação, Extrato) deve reagir e se reconstruir automaticamente quando o usuário alterar a matrícula ativa no seletor do topo da tela.

## 4. Estrutura das Telas (Blueprints)

### 4.1. Autenticação (Login / Primeiro Acesso)
* **Layout:** Centralizado (`grid place-items-center`, `min-h-screen`).
* **Visual:** Card flutuante (`shadow-xl`). Inputs com foco em anel azul (`focus:ring-2 focus:ring-blue-500`).
* **Ações:** Formulário de login principal e link destacado para "Primeiro Acesso / Criar Senha".

### 4.2. Dashboard Principal
* **Header:** Saudação ao usuário e o **Seletor de Vínculo** (Dropdown ou Tabs para alternar entre as matrículas).
* **Cards de Resumo (Grid):**
  * Card de Margem Disponível (Destaque máximo, tipografia grande, cor Esmeralda).
  * Card de Margem Utilizada.
  * Card de Contratos Ativos.
* **Quick Actions:** Botões de atalho rápido (ex: "Simular Empréstimo") com ícones grandes e interativos (`hover:-translate-y-1 transition-transform`).

### 4.3. Simulação e Contratação
* **Layout:** Grid de 2 colunas no Desktop (`grid-cols-1 md:grid-cols-2`), empilhado no Mobile.
* **Coluna de Interação (Esquerda):** Inputs do tipo `range` (Slider) para o valor desejado e `select` ou botões radio para o número de parcelas.
* **Coluna de Resultado (Direita):** Card de resumo da simulação (Valor da Parcela, Taxa, CET). 
* **Validação Visual:** Se a parcela da simulação for MAIOR que a Margem Disponível da matrícula selecionada, o card de resultado deve ficar vermelho e o botão "Solicitar" deve ser desabilitado visualmente (`opacity-50 cursor-not-allowed`).

### 4.4. Meus Empréstimos (Extrato)
* **Visualização:** Lista de Cards (`flex flex-col gap-4`), abolindo tabelas tradicionais para garantir responsividade perfeita no celular.
* **Anatomia do Card:**
  * Cabeçalho do Card: Logo/Nome da Instituição Financeira e Status (Badge Tailwind: Verde para Averbado, Amarelo para Análise).
  * Corpo do Card: Valor total e valor da parcela.
  * Rodapé do Card: Barra de progresso visual indicando a evolução do pagamento (ex: 12 de 48 parcelas pagas).

### 4.5. Perfil e Configurações
* **Layout:** Navegação em Abas (Tabs).
* **Abas:**
  1. **Dados Pessoais:** Inputs `readonly` com informações do usuário.
  2. **Vínculos:** Lista descritiva de todas as matrículas atreladas ao CPF (Órgão, Data de Admissão, Cargo).
  3. **Segurança:** Formulário simples para alteração de senha.