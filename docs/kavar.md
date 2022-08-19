# Kavar - a linguagem de programação

## Introdução

---

## Hello, Kavar

O código abaixo exibe uma frase na tela:

`print("Hello world");`

---

## Tipos de Dados

* **Boolean**: adiciona lógica à linguagem, podendo assumir entre os valores falso ou verdadeiro.
  * `false;`
  * `true;`
* **Números**: Kavar possui somente um tipo de dados, number, que é o ponto fluante de dupla precisão.
  * `1234;`
  * `12.648;`
* **Strings**: tratar textos é uma tarefa essencial de qualquer linguagem. As strings serão identificadas sempre que estiverem dentro de aspas duplas.
  * `"String de texto";`
  * `""; // indica uma string vazia.`
  * `"1234"; // continua sendo uma string.`
* **Nil**: um tipo de dados vazio, que não representa nada. Utilizou-se nil ao invés de null porque a segunda palavra já está reservada para a linguagem Java.

Conforme pode ser visto, utilize as duas barras (`//`) para indicar que se trata de um comentário.

O fim de um comentário se derá pela quebra de linha (`\n` ou `\r`).

---

## Variáveis

Como as variáveis na linguagem são fortemente tipadas, será necessário especificar o tipo da informação para alocar o respectivo espaço na memória.

Caso o tipo da variável for omitido, resultará em erro de compilação.

Para declarar uma variável com um valor inicial:

* `number pi := 3.14; // para declarar uma variável do tipo number.`
* `string text := "That's a text"; // para declarar uma string.`
* `bool isTrue := !false; // para declarar uma variável booleana.`

O tipo de dados *Nil* pode ser atribuído para *number* e *string*, mas não pode ser atribuído ao tipo *bool*.

**Obs**: a atribuição de dados é feita através do operador `:-`.

---

## Expressões

### *Aritmética*

As operações comumente utilizadas por outras linguagens também estão presentes na linguagem Kavar. São elas:

* `14 + 2;`
* `12 - 9;`
* `45 * 1;`
* `5 / 4;`

Tentar dividir um número diretamente por 0 resultará em erro de compilação, mas interromperá a aplicação caso aconteça em tempo de execução.

Não é possível utilizar expressões aritméticas em valores *booleanos*, mas será possível com *strings*.

### *Comparação e Equivalência*

```c
menor < que;
menorQue <= ouIgualA;
maior > que;
maiorQue >= ouIgualA;
```

Para testar equivalência ou desigualdade:

```c
11 == 2; // resulta em falso.`
"house" != "casa"; // resulta em verdadeiro.
```

Comparar valores de tipos diferentes nunca resultará em verdadeiro, pois não haverá conversões implícitas de variáveis:

`3.14 == "pi"; // resulta em falso.`

### *Operadores Lógicos*

Há três operadores lógicos disponíveis, são eles:

* `!isActive; // negação. Ou seja, vira verdadeiro caso seja falso.`
* `true && false; // indica o operador lógico E.`
* `true || false; // indica o operador lógico OU.`

### *Precedência e Agrupamento*

`number media = (minimo + maximo) / 2;`

1. parênteses.
2. negação.
3. 

---

## Controles de fluxo

```c
if (condition) {
    print("yes");
} else {
    print("no");
}
```

```c
while (b < 4) {
    print(b);
    b := b + 1;
}
```

```c
for (number a := 1; a < 10; a := a + 1) {
    print(a)
}
```

A variável do laço for pode ser declarada junto com a estrutura de controle de fluxo.

## Funções

```c
arrumarCasa()
```

---

## Regras Avançadas
