\\ASM DATA=2000 EXTRA=0 STACK=4000
// ________________________
//   BUSCAMINAS
// ________________________
//    A B C D E F G H I J
// 00 . . . . . . . . . .
// 01 . . . . . . . . . .
// 02 . . . . . . . . . .
// 03 . . . . . . . . . .
// 04 . . . . . . . . . . 
// 05 . . . . . . . . . .
// 06 . . . . . . . . . .
// 07 . . . . . . . . . .
// 08 . . . . . . . . . .
// 09 . . . . . . . . . .
// ________________________
//
// se accede con letra y numero ej: B6
// 
// La matriz se guarda como un vector de filas   
// cálculo de la celda: B6
// '6' - '0' = 6 ; 6 * Columnas = 60
// 'B' - 'A' = 1 ; 1 + 60 = 61 << [61+tablero] está el valor 

title       equ     "  BUSCAMINAS  "
msgremain   equ     "Faltan descubrir: "
UNPICK      equ     '.' //(CONSTANTE) Simbolo para representar minas
EMPTY       equ     ' ' //(CONSTANTE) Simbolo para un espacio vacío
MINE        equ     '*' //(CONSTANTE) Simbolo para representar una mina
MINES       equ     20  //(CONSTANTE) Cantidad de minas en el tablero 
COLUMNAS    equ     15  //(CONSTANTE) Cantidad de columans del tablero (max 26)
FILAS       equ     15  //(CONSTANTE) Cantidad de filas del tablero (max 26)
tablero     equ     0   //(DIR DE MEMORIA) Matriz de FILASxCOLUMNAS donde se encuentra el tablero 
visible     equ     700 //(DIR DE MEMORIA) Representación del tablero por pantalla 
remain      equ     1400 //(DIR DE MEMORIA) Cantidad de celdas que falta "destapar" (y no tengan minas)
buffer      equ     1401 //(DIR DE MEMORIA) auxiliar para caracteres para imprimir
true        equ     -1  //Representación del verdadero
false       equ     0   //Representación del falso 
//--------------------------------------------------------------------------------------------
// COMENZAMOS...
//--------------------------------------------------------------------------------------------
            push    visible
            call    IniVis          //Inicializa la Matriz visible (Todo UNPICK)
            add     sp, 1

            push    tablero
            call    INITAB          //Inicializa la Matriz del tablero (donde está la información oculta)
            add     sp, 1               

            push    tablero 
            call    CALCMINES       //Pone los números con la cantidad de MINEs al rededor en cada celda
            add     sp, 1

            //push    tablero           //Para previsualizar el tablero (si se quita el comentario es hacer trampa)
            //call    SHOW
            //add     sp, 1
            //stop 
        
            //en remain guarda la cantidad de celdas que le faltan destapar
            //para para mostrar al usuario 
            //acá se establece un valor inicial que es:
            //el tamño de la matriz menos la cantidad de minas
            mov     [remain], FILAS     
            mul     [remain], COLUMNAS
            sub     [remain], MINES
            //este valor luego se actualiza en cada movimiento contando las celdas que faltan destapar
        
sigmov:     call    PRINTBAR 

            mov     dx, title 
            mov     bx, 1
            mov     ax, %1000
            sys     20 
            
            call    PRINTBAR

            push    visible
            call    show
            add     sp, 1

            call    PRINTBAR

            mov     dx, msgremain 
            mov     bx, 1
            mov     ax, %1100
            sys     20 

            mov     dx, remain
            mov     cx, 1
            mov     ax, %1001
            sys     2

            call    leemov          //setea en CX y FX la columna y fila que seleccionó el usuario 

            push    visible
            push    fx
            push    cx
            push    tablero
            call    PICKUP          //destapa la celda (copia tablero[c,f] -> visible[c,f] y si es EMPTY se llama recursivamente) 
            add     sp, 4           //devuelve en AX true si encontró una MINE 

            cmp     ax, true               
            jz      loser           //si explotó perdió
                                    //si no explotó:
            push    UNPICK          //UNPICK es el símbolo usado para representar las celdas no destapadas 
            push    visible         //Matriz visible 
            call    MCOUNT          //cuenta la cantidad de celdas que falta destapar... 
            add     sp, 2

            sub     ax, MINES       //... y le resta la cantidad de MINEs y de este modo sabe cuantas faltan
            mov     [remain], ax
            
            cmp     ax, 0           //Si no le falta ninguna es porque ya ganó !! 
            jz      winner
            jn      winner          //(por las dudas, capás se pasó)

            jmp     sigmov          //Si aun le quedan tendrá que seguir jugando

/// Si perdió
lost        equ     "  PERDISTE :( "
loser:      push    visible
            push    tablero
            call    SHOWMINES       // Pone las MINEs en el tablero visible
            add     sp, 2

            push    visible
            call    show            // Muestra el tablero visible 
            add     sp, 1

            call    PRINTBAR

            mov     dx, lost 
            mov     bx, 1
            mov     ax, %1000
            sys     20 

            call    PRINTBAR
            
            stop 

win         equ     "  GANASTE! :) "
winner:     push    visible
            push    tablero
            call    SHOWMINES
            add     sp, 2

            push    visible
            call    show
            add     sp, 1

            call    PRINTBAR
            
            mov     dx, win 
            mov     bx, 1
            mov     ax, %1000
            sys     20 

            call    PRINTBAR
            
            stop 
          
//--------------------------------------------------------------------------------------------
// MCOUNT 
//--------------------------------------------------------------------------------------------
// Cuenta las ocurrecias de un valor en una matriz 
// Parametros: (1) ^Matriz (2) valor buscado 
// Devuelve: en AX la cantidad de ocurrencias
//--------------------------------------------------------------------------------------------
MCOUNT:         push    bp
                mov     bp, sp 
                push    bx  //Apunta a la celda 
                push    cx 
                // Usa la matriz como un vector!! (porque la tiene que recorrer de principio a fin, sin importar columna y fila)

                mov     ax, 0       //acumulador y respuesta 
                mov     bx, [BP+2]  //Inicio
                mov     cx, FILAS
                mul     cx, COLUMNAS 
                add     cx, bx      //En CX queda apuntando al final+1
                sub     bx, 1
MCOUNTsig:      add     bx, 1
                cmp     bx, cx
                jz      MCOUNTend
                cmp     [bx], [BP+3]
                jnz     MCOUNTsig
                add     ax, 1
                jmp     MCOUNTsig

MCOUNTend:      pop     cx
                pop     bx
                mov     sp, bp
                pop     bp
                ret 


//--------------------------------------------------------------------------------------------
// PICKUP
//--------------------------------------------------------------------------------------------
// destapa una celda, si está vacía destapa las circundantes. 
// Parámetros: (1) matriz tablero (2) columna (3) fila (4) matriz visible
// Devuelve: AX = false si está ok o true si perdió 
//--------------------------------------------------------------------------------------------
PICKUP:     push    bp
            mov     bp, sp
            push    ac  //guarda la respuesta (al final se asigna a AX)
            push    cx  //columna
            push    fx  //fila  

            mov     cx, [BP+3]
            mov     fx, [BP+4]
            mov     ac, false    //está todo bien hasta que se demuestre lo contrario

            // si está fuera de rango: salida silenciosa (no hace nada)
            cmp     cx, 0 
            jn      pickupfin 
            cmp     cx, COLUMNAS
            jz      pickupfin 
            jp      pickupfin 
            cmp     fx, 0 
            jn      pickupfin 
            cmp     fx, FILAS 
            jz      pickupfin 
            jp      pickupfin 
            
            // veo primero que hay en visibles 
            push    fx
            push    cx
            push    [bp+5]
            call    MGET
            add     sp, 3

            cmp     ax, UNPICK 
            jnz     pickupfin // si encuentra algo diferente del UNPICK quiere decir que ya fue destapado => salida silenciosa 

            push    fx
            push    cx
            push    [BP+2]
            call    MGET
            add     sp, 3

            cmp     ax, MINE  
            jz      PICKUPbomb      // si encuentra una MINE perdió 

            push    ax 
            push    fx
            push    cx
            push    [BP+5]
            call    MSET            // setea la matriz visible con lo que encontró 
            add     sp, 4       

            cmp     ax, EMPTY 
            jnz     pickupfin       // si encontró un EMPTY debe destapar los circundantes, sino se va            
            
            push    [BP+5]
            push    fx
            push    cx 
            push    [BP+2]
            call    PICKUPARROUND   // Esta subrutina a su vez llama a piskup recursivamente 
            add     sp, 4            

            jmp     PICKUPfin 
            
PICKUPbomb: mov     ac, true 

PICKUPfin:  mov     ax, ac 
            pop     fx
            pop     cx
            pop     ac
            mov     sp, bp
            pop     bp
            ret          

//--------------------------------------------------------------------------------------------
// PICKUPARROUND
//--------------------------------------------------------------------------------------------
// Destapa todas las celdas al redodeor de una 
// parametro (1) matriz tablero (2) columna (3) fila (4) matriz visible
//--------------------------------------------------------------------------------------------
PICKUPARROUND:      push    bp 
                    mov     bp, sp 
                    push    fx      //fila 
                    push    bx      //fila tope 
                    push    cx      //columna 
                    push    ac      //columna inicial
                    push    dx      //columna tope 

                    mov     fx, [BP+4]
                    mov     cx, [BP+3]

                    mov     bx, fx
                    add     bx, 2   // +2 para salir por JZ
                    mov     dx, cx
                    add     dx, 2   // +2 para salir por JZ
                    mov     ac, cx
                    sub     ac, 2   // -2 para incrementar al entrar en el ciclo 

                    sub     fx, 2   // -2 para incrementar al entrar en el ciclo 
PICKUPARROUNDCOL:   add     fx, 1
                    cmp     fx, bx
                    jz      PICKUPARROUNDEND

                    mov     cx, ac 
PICKUPARROUNDFIL:   add     cx, 1
                    cmp     cx, dx 
                    jz      PICKUPARROUNDCOL

                    push    [BP+5]
                    push    fx
                    push    cx 
                    push    [BP+2]
                    call    pickup
                    add     sp, 4

                    jmp     PICKUPARROUNDFIL
PICKUPARROUNDEND:   pop     dx
                    pop     ac
                    pop     cx                     
                    pop     bx
                    pop     fx
                    mov     sp, bp 
                    pop     bp 
                    ret


//--------------------------------------------------------------------------------------------
// MGET
//--------------------------------------------------------------------------------------------
// Devuelve en AX el valor de una matriz 
// parametro (1) matriz (2) columna (3) fila 
//--------------------------------------------------------------------------------------------
MGET:       push    bp
            mov     bp, sp
            push    bx  
            push    cx  
            push    fx  
            
            mov     bx, [BP+2] //base de la matriz tablero 
            mov     cx, [BP+3] //columna 
            mov     fx, [BP+4] //fila 

            mul     fx, filas 
            add     bx, fx
            add     bx, cx

            mov     ax, [bx]
MGETfin:    pop     fx
            pop     cx
            pop     bx
            mov     sp, bp
            pop     bp
            ret

//--------------------------------------------------------------------------------------------
// MSET
//--------------------------------------------------------------------------------------------
// Agrega un valor a una matriz 
// parametro (1) matriz (2) columna (3) fila  (4) valor 
//--------------------------------------------------------------------------------------------
MSET:       push    bp
            mov     bp, sp
            push    bx  
            push    cx  
            push    fx  
            
            mov     bx, [BP+2] //base de la matriz tablero 
            mov     cx, [BP+3] //columna 
            mov     fx, [BP+4] //fila 

            mul     fx, filas 
            add     bx, fx
            add     bx, cx

            mov     [bx], [BP+5]
            
MSETfin:    pop     fx
            pop     cx
            pop     bx
            mov     sp, bp
            pop     bp
            ret


//--------------------------------------------------------------------------------------------
// LEEMOV   
//--------------------------------------------------------------------------------------------
// Lee un moviento del usuario
// Devuelve en fx la fila y en cx la columna
//--------------------------------------------------------------------------------------------
msgcol      equ     "Columna: " 
msgfil      equ     "Fila: " 
msgerror    equ     "ERROR: fuera de rango!" 
leemov:     push    bp
            mov     bp, sp
            sub     sp, 1
            push    ax
            push    bx  
            push    dx  

            mov     fx, -1
            mov     cx, -1
leecol:     mov     dx, msgcol
            mov     bx, 1
            mov     ax, %1100
            sys     20 

            mov     dx, buffer
            mov     bx, 2
            mov     ax, %1000
            sys     10 
            
            mov     [BP-1], [DS:buffer]
            or      [BP-1], ' ' //paso a minuscula
            sub     [BP-1], 'a' //paso a número 
            cmp     [BP-1], 0
            jn      leeerror
            cmp     [BP-1], columnas 
            jz      leeerror 
            jp      leeerror 
            
            mov     cx, [BP-1]

leefil:     mov     dx, msgfil
            mov     bx, 1
            mov     ax, %1100
            sys     20 

            push    cx
            mov     dx, buffer
            mov     cx, 1
            mov     ax, %1000
            sys     1 
            pop     cx

            mov     [BP-1], [DS:buffer]
            cmp     [BP-1], 0
            jn      leeerror
            cmp     [BP-1], FILAS
            jz      leeerror 
            jp      leeerror 
            
            mov     fx, [BP-1]
            jmp     leemovfin

leeerror:   push    cx
            mov     dx, msgerror
            mov     bx, 1
            mov     cx, -1
            mov     ax, %1000
            sys     20
            pop     cx 
            cmp     cx, -1
            jz      leecol
            jmp     leefil


leemovfin:  pop     dx
            pop     bx
            pop     ax
            mov     sp, bp
            pop     bp
            ret


//--------------------------------------------------------------------------------------------
// CALCMINES
//--------------------------------------------------------------------------------------------
// Recorrer cada posición de tablero 
// si no tiene MINE debe poner el número de la suma 
// de los MINE de alrededor. 
// Paramtros: (1) posicion del tablero 
//--------------------------------------------------------------------------------------------
CALCMINES:          push    bp
                    mov     bp, sp
                    push    ax
                    push    cx  // indice de columna
                    push    fx  // indice de fila 
            
                    mov     fx, -1 //indicie de fila 
CALCMINESfil:       add     fx, 1
                    cmp     fx, FILAS 
                    jz      CALCMINESend

                    mov     cx, -1 //indice de columna 
CALCMINEScol:       add     cx, 1
                    cmp     cx, COLUMNAS
                    jz      CALCMINESfil

                    push    fx              // Obtiene lo que hay en esa posición 
                    push    cx
                    push    [BP+2]
                    call    MGET
                    add     sp, 3

                    cmp     ax, MINE        
                    jz      CALCMINEScol    // Si hay una mina no hace nada y sigue   

                    push    fx              // Si no hay mina, tiene que ver cuantas hay al redeor 
                    push    cx
                    push    [BP+2]
                    call    COUNTARROUND    // devuelve la cantidad de minas circundantes
                    add     sp, 3

                    cmp     ax, 0           // AX tiene la cantidad de minas
                    jz      CALCMINESempty
                    add     ax, '0'         // pasa a caracter
                    jmp     CALCMINESset        
CALCMINESempty:     mov     ax, EMPTY       // Si no hay minas al rededor pone un EMPTY 
CALCMINESset:       push    ax              // Setea en la posición actual el caracter con la cantidad de minas
                    push    fx
                    push    cx
                    push    [BP+2]
                    call    MSET 
                    add     sp, 4
                    
                    jmp     CALCMINESCOL
CALCMINESend:       pop     fx
                    pop     cx
                    pop     ax 
                    mov     sp, bp
                    pop     bp
                    ret

//--------------------------------------------------------------------------------------------
// COUNTARROUND
//--------------------------------------------------------------------------------------------
// Cuenta la cantidad de minas al rededor de una celda 
// Parametros: (1) ^Matriz (2) columna (3) fila
// Devuelve: en AX la cantidad de minas 
//--------------------------------------------------------------------------------------------
COUNTARROUND:       push    bp 
                    mov     bp, sp 
                    push    ac      //acumula la cantidad de minas 
                    push    fx      //fila 
                    push    bx      //fila tope 
                    push    cx      //columna 
                    push    ex      //columna inicial
                    push    dx      //columna tope 

                    mov     ac, 0 
                    mov     fx, [BP+4]
                    mov     cx, [BP+3]

                    mov     bx, fx
                    add     bx, 2   // +2 para salir por JZ
                    mov     dx, cx
                    add     dx, 2   // +2 para salir por JZ
                    mov     ex, cx
                    sub     ex, 2   // -2 para incrementar al entrar en el ciclo 

                    sub     fx, 2   // -2 para incrementar al entrar en el ciclo 
COUNTARROUNDcol:    add     fx, 1
                    cmp     fx, bx
                    jz      COUNTARROUNDend

                    mov     cx, ex 
COUNTARROUNDfil:    add     cx, 1
                    cmp     cx, dx 
                    jz      COUNTARROUNDcol

                    push    fx
                    push    cx
                    push    [BP+2] 
                    call    HASMINE
                    add     sp, 3

                    add     ac, ax

                    jmp     COUNTARROUNDfil
COUNTARROUNDend:    mov     ax, ac
                    pop     dx
                    pop     ex
                    pop     cx                     
                    pop     bx
                    pop     fx
                    pop     ac
                    mov     sp, bp 
                    pop     bp 
                    ret


//--------------------------------------------------------------------------------------------
// HASMINE
//--------------------------------------------------------------------------------------------
// Dada una posición (col,fil) de una matriz
// en AX: 
// devuelve 1 si tiene MINE 
// devuelve 0 si no tiene MINE 
// devuelve 0 si está fuera de rango
// Parametros: (1) ^Matriz, (2) Columna, (3) Fila 
//--------------------------------------------------------------------------------------------
HASMINE:    push    bp
            mov     bp, sp
            push    ac  // contiene la salida que se copia a AX al final. 
            push    cx  // columna
            push    fx  // fila 

            mov     ac, 0      // salida por default  
            mov     fx, [BP+4]
            mov     cx, [BP+3]
            
            cmp     cx, 0      // si la columna está fuera de rango <0 o >9 se va 
            jn      HASMINEend
            cmp     cx, COLUMNAS
            jz      HASMINEend
            jp      HASMINEend 
            
            cmp     fx, 0      // si la fila  está fuera de rango <0 o >9 se va 
            jn      HASMINEend
            cmp     fx, FILAS 
            jz      HASMINEend
            jp      HASMINEend 

            push    fx         // Obtiene el contenido de la celda
            push    cx
            push    [BP+2]
            call    MGET
            add     sp, 3

            cmp     ax, MINE
            jnz     HASMINEend
            mov     ac, 1       // Si es una mina devuelve 1

HASMINEend: mov     ax, ac
            pop     fx
            pop     cx
            pop     ax
            mov     sp, bp
            pop     bp
            ret

//--------------------------------------------------------------------------------------------
// SHOW 
//--------------------------------------------------------------------------------------------
// muestra un tablero 
// Parametros: (1) ^Matriz del tablero 
//--------------------------------------------------------------------------------------------
letras      equ     "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
margen      equ     "    "
SHOW:       push    bp 
            mov     bp, sp
            push    ax
            push    bx
            push    cx  //columna
            push    fx  //fila 
            push    dx
            
            mov     fx, -1      // inicializa fila  

            // Arma la lista de columnas 
            mov     dx, margen  // agrega un margen 
            mov     bx, 1
            mov     ax, %1100
            sys     20             

            mov     bx, letras
            mov     dx, bx
            add     dx, COLUMNAS 
SHOWcolt:   cmp     bx, dx
            jz      showFil

            push    [CS:bx]
            call    print 
            add     sp, 1
            add     bx, 1
            jmp     SHOWcolt           
 
SHOWfil:    add     fx, 1           // Recorre por fila e imprime la matriz
            cmp     fx, FILAS
            jz      SHOWend

            call    println         //imprime un espacio para bajar de linea. 

            mov     dx, buffer      //imprime nro de fila 
            mov     [dx+0], ' '
            mov     ax, fx
            div     ax, 10          //Se asume que nunca va a ser mas de 99
            add     ax, '0' 
            mov     [dx+1], ax 
            mov     ax, fx
            mod     ax, 10 
            add     ax, '0' 
            mov     [dx+2], ax 
            mov     [dx+3], ' ' 
            mov     [dx+4], %0
            mov     bx, 2
            mov     ax, %1100
            sys     20          

            mov     cx, -1 
SHOWcol:    add     cx, 1
            cmp     cx, COLUMNAS
            jz      SHOWfil

            push    fx
            push    cx
            push    [BP+2]
            call    MGET
            add     sp, 3

            push    ax
            call    print 
            add     sp, 1

            jmp     SHOWcol
SHOWend:    call    println              
            pop     dx
            pop     fx
            pop     cx 
            pop     bx
            pop     ax             
            mov     sp, bp
            pop     bp 
            ret

//--------------------------------------------------------------------------------------------
// PRINT   
//--------------------------------------------------------------------------------------------
// Imprime un caracter seguido de un espacio
// Parametros: (1) caracter a imprimir 
//--------------------------------------------------------------------------------------------
PRINT:      push    bp  //si si solo muestra los * 
            mov     bp, sp 
            push    ax
            push    bx
            push    dx 
            mov     dx, buffer
            mov     [dx+0], [BP+2]
            mov     [dx+1], ' '
            mov     [dx+2], %0
            mov     bx, 2
            mov     ax, %1100
            sys     20 
            pop     dx
            pop     bx
            pop     ax
            mov     sp, bp 
            pop     bp
            ret     

//--------------------------------------------------------------------------------------------
// PRINTLN
//--------------------------------------------------------------------------------------------
// Baja de linea
//--------------------------------------------------------------------------------------------
PRINTLN:    push    ax
            push    bx
            push    dx 
            mov     [buffer], %0 
            mov     dx, buffer
            mov     bx, 2
            mov     ax, %1000
            sys     20 
            pop     dx
            pop     bx
            pop     ax
            ret

//--------------------------------------------------------------------------------------------
// PRINTBAR
//--------------------------------------------------------------------------------------------
// Imprime un aline según la cantidad de columnas 
// que tenga el tablero 
//--------------------------------------------------------------------------------------------
BARSIMB         equ     '_'
PRINTBAR:       push    ax
                push    bx
                push    cx
                push    dx 
            
                mov     dx, buffer      //donde pre-escribe
                mov     cx, COLUMNAS 
                mul     cx, 2 
                add     cx, 4           //agregar el margen
                add     cx, dx

PRINTBARsig:    mov     [dx], BARSIMB   //asume que va a ser mas de 1 
                add     dx, 1
                cmp     dx, cx
                jnz     PRINTBARsig 

                mov     [dx], %0
                mov     dx, buffer
                mov     bx, 2
                mov     ax, %1000
                sys     20 
                pop     dx
                pop     cx
                pop     bx
                pop     ax
                ret

//--------------------------------------------------------------------------------------------
// SHOWMINES
//--------------------------------------------------------------------------------------------
// Agrega al vector visible la ubicación de las minas
// Parametros: (1) ^Matriz Tablero (2) ^Matriz Visible
//--------------------------------------------------------------------------------------------
SHOWMINES:      push    bp
                mov     bp, sp  
                push    cx  //indice de columna
                push    fx  //indice de fila 

                mov     fx, -1 
SHOWMINESfil:   add     fx, 1
                cmp     fx, FILAS
                jz      SHOWMINESend

                mov     cx, -1 
SHOWMINEScol:   add     cx, 1
                cmp     cx, COLUMNAS
                jz      SHOWMINESfil

                push    fx
                push    cx
                push    [BP+2]
                call    MGET             // Obtiene el valor de la celda del tablero 
                add     sp, 3

                cmp     ax, MINE
                jnz     SHOWMINEScol    // Si no es MINE va por la siguente celda 

                push    ax              
                push    fx
                push    cx
                push    [BP+3]          // Si es MINE setea en la celda visible 
                call    MSET
                add     sp, 4  

                jmp     SHOWMINEScol            

SHOWMINESend:   pop     fx
                pop     cx
                mov     sp, bp
                pop     bp 
                ret 

//--------------------------------------------------------------------------------------------
// INITAB
//--------------------------------------------------------------------------------------------
// Inicializa en forma aletoria la ubicación de las minas en el tablero 
// Parametros: (1) ^Matriz Tablero 
//--------------------------------------------------------------------------------------------
INITAB:         push    bp
                mov     bp, sp 
                push    ax
                push    bx
                push    cx 
                // Usa la matriz como un vector!! 
                mov     bx, [BP+2]  //Inicio
                mov     cx, FILAS
                mul     cx, COLUMNAS 
                add     cx, bx      //En CX queda apuntando al final+1

INITABempty:    cmp     bx, cx
                jz      INITABmine
                mov     [bx], EMPTY
                add     bx, 1
                jmp     INITABempty

INITABmine:     mov     ax, MINES   //Ahora en ax pone la cantidad de MINEs 
                sub     cx, [BP+2] 
                sub     cx, 1       //Ahora en cx queda el número max de celdas

INITABadd:      cmp     ax, 0       //Cuando ax llegue a 0, termino de ubicar las MINEs  
                jz      INITABend
                rnd     bx, cx      //sortea la ubicación de un MINE
                add     bx, [bp+2]  //le agrega la base del vector 
                cmp     [bx], MINE  //se fija que no haya ya un MINE en ese ubicación 
                jz      INITABadd   //si hay, vuelve a sortear sin decrementar
                mov     [bx], MINE  //si no hay ubica la MINE en la posición  
                sub     ax, 1       //decrementa la cantidad de MINEs...     
                jmp     INITABadd   //...y vuelve 

INITABend:      pop     cx
                pop     bx
                pop     ax 
                mov     sp, bp
                pop     bp
                ret 


// Inicializa matriz visible (usa constantes FILAS y COLUMNAS)
// Parametros: (1) ^Matriz 
IniVis:     push    bp
            mov     bp, sp  
            push    cx  //indice de columna
            push    fx  //indice de fila 

            mov     fx, -1 
IniVisFil:  add     fx, 1
            cmp     fx, FILAS
            jz      IniVisEnd

            mov     cx, -1 
IniVisCol:  add     cx, 1
            cmp     cx, COLUMNAS
            jz      IniVisFil

            push    UNPICK
            push    fx
            push    cx
            push    [BP+2]
            call    MSET
            add     sp, 4

            jmp     IniVisCol            

IniVisEnd:  pop     fx
            pop     cx
            mov     sp, bp
            pop     bp 
            ret 