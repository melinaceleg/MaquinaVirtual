#include <stdio.h>
#include <stdlib.h>
#include <ctype.h>
#include <string.h>
#include <math.h>
#include <time.h>

#define PS 0

#define CS 1
#define DS 2
#define ES 3

#define IP 4

#define SS 5
#define SP 6
#define BP 7

#define AC 8
#define CC 9
#define AX 10
#define BX 11
#define CX 12
#define DX 13
#define EX 14
#define FX 15

#define ERROR_STACK_OVERFLOW 1
#define ERROR_STACK_UNDERFLOW 2

#define TAM_MEMORIA 8192
#define TAM_REG 16
#define TAM_MEMONICOS 150

typedef struct
{
    int RAM[TAM_MEMORIA];
    int REG[TAM_REG];
}TMemoria;

typedef char TDireccionImagen[100];
typedef TDireccionImagen* TDireccionesImagenes;

typedef struct
{
    TDireccionesImagenes direccionesImagenes;
    int cantidadImagenes;
}TEntrada;

typedef int TFlags[4]; //a, b, c, d

//////////////////////////////////////////////////////////////////////////////////////////

typedef int (*T_FUNC)(TMemoria *memoria, TFlags flags, int *arg1, int *arg2);

int func_MOV(TMemoria *memoria, TFlags flags, int *arg1, int *arg2);
int func_ADD(TMemoria *memoria, TFlags flags, int *arg1, int *arg2);
int func_SUB(TMemoria *memoria, TFlags flags, int *arg1, int *arg2);
int func_MUL(TMemoria *memoria, TFlags flags, int *arg1, int *arg2);
int func_DIV(TMemoria *memoria, TFlags flags, int *arg1, int *arg2);
int func_MOD(TMemoria *memoria, TFlags flags, int *arg1, int *arg2);
int func_CMP(TMemoria *memoria, TFlags flags, int *arg1, int *arg2);
int func_SWAP(TMemoria *memoria, TFlags flags, int *arg1, int *arg2);
int func_RND(TMemoria *memoria, TFlags flags, int *arg1, int *arg2);
int func_AND(TMemoria *memoria, TFlags flags, int *arg1, int *arg2);
int func_OR(TMemoria *memoria, TFlags flags, int *arg1, int *arg2);
int func_NOT(TMemoria *memoria, TFlags flags, int *arg1, int *arg2);
int func_XOR(TMemoria *memoria, TFlags flags, int *arg1, int *arg2);
int func_SHL(TMemoria *memoria, TFlags flags, int *arg1, int *arg2);
int func_SHR(TMemoria *memoria, TFlags flags,int *arg1, int *arg2);
int func_JMP(TMemoria *memoria, TFlags flags, int *arg1, int *arg2);
int func_JE(TMemoria *memoria, TFlags flags, int *arg1, int *arg2);
int func_JG(TMemoria *memoria, TFlags flags, int *arg1, int *arg2);
int func_JL(TMemoria *memoria, TFlags flags, int *arg1, int *arg2);
int func_JZ(TMemoria *memoria, TFlags flags, int *arg1, int *arg2);
int func_JP(TMemoria *memoria, TFlags flags, int *arg1, int *arg2);
int func_JN(TMemoria *memoria, TFlags flags, int *arg1, int *arg2);
int func_JNZ(TMemoria *memoria, TFlags flags, int *arg1, int *arg2);
int func_JNP(TMemoria *memoria, TFlags flags, int *arg1, int *arg2);
int func_JNN(TMemoria *memoria, TFlags flags, int *arg1, int *arg2);

int func_PUSH(TMemoria *memoria, TFlags flags, int *arg1, int *arg2);
int func_POP(TMemoria *memoria, TFlags flags, int *arg1, int *arg2);
int func_CALL(TMemoria *memoria, TFlags flags, int *arg1, int *arg2);
int func_RET(TMemoria *memoria, TFlags flags, int *arg1, int *arg2);

int func_SLEN(TMemoria *memoria, TFlags flags, int *arg1, int *arg2);
int func_SMOV(TMemoria *memoria, TFlags flags, int *arg1, int *arg2);
int func_SCMP(TMemoria *memoria, TFlags flags, int *arg1, int *arg2);

int func_SYS(TMemoria *memoria, TFlags flags, int *arg1, int *arg2);
int func_STOP(TMemoria *memoria, TFlags flags, int *arg1, int *arg2);

//////////////////////////////////////////////////////////////////////////////////////////

void CargarImagen(TMemoria *memoria, char *url);
int CargarImagenes(TMemoria *memoria, TEntrada entrada);

void CargarMnemonicos(T_FUNC *mnemonicos);

void EjecutarMemoria(TMemoria *memoria, TFlags flags);
int EjecutarPrograma(TMemoria *memoria, T_FUNC mnemonicos[TAM_MEMONICOS], TFlags flags);
int EjecutarInstruccion(TMemoria *memoria, T_FUNC* mnemonicos, TFlags flags, int tMnemonico, int tOper1, int Oper1, int tOper2, int Oper2);

int CalcularCantidadDigitos(int base, int num);
void MostrarConEspacio(int base, int num, int posicionEspacio, int cantDigitos);
void MostrarDireccion(int base, int numero, int espacio, int cantDigitos);

void MostrarArgumento(int primerArgumento, int tOper, int oper);
int MnemonicoCantArgumentos(int codMnemonico);
void CargarStringMnemonicos(char *StringMmemonicos[]);
void MostrarCodigoAssembler(TMemoria memoria, int marcar);
void MostrarErrores(int error);

int InterpretarInstruccion(int celda);
void ObtenerInstruccion(TMemoria memoria, int i, int *codInstruccion, int *codOperando1, int *codOperando2);

void tipoOperandos(TMemoria memoria, int celda, int *tipo, int *operando);
void ObtenerOperandos(TMemoria memoria, int Instancia, int *tOper1, int *Oper1, int *tOper2, int *Oper2);


int VerificarArchivo(char *direccion);
int VerificarFlags(char *cadena);

void LeerEntrada(TEntrada *entrada, TFlags *flags, int arge, char *arg[]);
int PasarAMemoria(TMemoria *principal, TMemoria aux, int cantImagenes, int numPrograma);

void AjustarRegistro(int *RAM, int indice, int valor);
int CalcularValoresRegistros(TMemoria *memoria, int cantImagenes, int indice);

void CopiarRegistros(int *principal, int prinP, int *secundario, int prinS);
//////////////////////////////////////////////////////////////////////////////////////////

int getIP(TMemoria memoria);
void setIP(TMemoria *memoria, int ip);
char getASCII(int num);
int getLinea(int num);
char *getNombreDelRegistro(int i);
//////////////////////////////////////////////////////////////////////////////////////////
void MostrarFlagsA(TFlags flags, TMemoria memoria);
void MostrarFlagsB(TFlags flags, TMemoria memoria);
void MostrarFlagsC(TFlags flags);
void MostrarFlagsD(TFlags flags, TMemoria memoria, int marcar);
//////////////////////////////////////////////////////////////////////////////////////////

int main(int arge, char *arg[])
{
    TMemoria memoria;
    TFlags flags;
    TEntrada entrada;

    LeerEntrada(&entrada, &flags, arge, arg);
    if (entrada.cantidadImagenes > 0)
    {
        if (!CargarImagenes(&memoria, entrada))
        {
            EjecutarMemoria(&memoria, flags);
        }
        else
        {
            printf("Memoria insuficiente");
        }
    }

    printf("\n_");
    return 0;
}
//////////////////////////////////////////////////////////////////////////////////////////

void CopiarRegistros(int *principal, int prinP, int *secundario, int prinS)
{
    for (int i = 0; i < TAM_REG; i++)
        principal[prinP + i] = secundario[prinS + i];
}
//////////////////////////////////////////////////////////////////////////////////////////

int VerificarArchivo(char *direccion)
{
    int respuesta = 0;
    FILE *archivo = fopen(direccion, "r");
    if (archivo)
    {
        fclose(archivo);
        respuesta = 1;
    }
    return respuesta;
}

int VerificarFlags(char *cadena)
{
    int respuesta = -1;
    char *flag = strtok(cadena, "-");
    if (strlen(flag) == 1)
    {
        switch (flag[0])
        {
            case 'a': respuesta = 0; break;
            case 'b': respuesta = 1; break;
            case 'c': respuesta = 2; break;
            case 'd': respuesta = 3; break;
        }
    }
    return respuesta;
}

void LeerEntrada(TEntrada *entrada, TFlags *flags, int arge, char *arg[])
{
    int i = 1, j = 0;
    int flag;
    char direcciones[arge][100];
    for (; i < arge; i++)
    {
        if (VerificarArchivo(arg[i]))
        {
            strcpy(direcciones[j], arg[i]);
            j++;
        }
        else
        {
            flag = VerificarFlags(arg[i]);
            if (flag != -1)
            {
                (*flags)[flag] = 1;
            }
        }
    }
    if (j > 0)
    {
        entrada->direccionesImagenes = (TDireccionesImagenes)malloc(sizeof(TDireccionImagen) * j);
        entrada->cantidadImagenes = j;
        for (i = 0; i < j; i++)
        {
            strcpy(entrada->direccionesImagenes[i], direcciones[i]);
        }
    }
}

//CARGA LA IMAGEN DEL ARCHIVO
void CargarImagen(TMemoria *memoria, char *url)
{
    FILE *imgFile = fopen(url, "rb");
    int i = 0;
    if (imgFile)
    {
        fread(memoria->REG, sizeof(int), 16, imgFile);

        while (!feof(imgFile))
            fread(&memoria->RAM[i++], sizeof(int), 1, imgFile);

        fclose(imgFile);
    }
}

void AjustarRegistro(int *RAM, int indice, int valor)
{
    if (RAM[indice] == -1)
        RAM[indice] = (indice < TAM_REG)? 500:RAM[indice - TAM_REG];
    else
        RAM[indice] += valor;
}

int CalcularValoresRegistros(TMemoria *memoria, int cantImagenes, int indice)
{
    int memoriaInsuficiente = 0,
    PSant = (indice > 2)? memoria->RAM[indice + PS - TAM_REG] + memoria->RAM[indice + CS - TAM_REG]  : 2 + TAM_REG * cantImagenes;

    memoria->RAM[indice + CS] = PSant;
    if (PSant + memoria->RAM[indice + PS] <= TAM_MEMORIA)
    {
        AjustarRegistro(memoria->RAM, indice + DS, PSant);
        AjustarRegistro(memoria->RAM, indice + ES, PSant);
        AjustarRegistro(memoria->RAM, indice + SS, PSant);
    }
    else
    {
        memoriaInsuficiente = 1;
    }

    return memoriaInsuficiente;
}

int PasarAMemoria(TMemoria *memoria, TMemoria aux, int cantImagenes, int numPrograma)
{
    int i = 0, indice = 2 + 16 * numPrograma, error = 0;

    CopiarRegistros(memoria->RAM, indice, aux.REG, 0);
    error = CalcularValoresRegistros(memoria, cantImagenes, indice);

    if (!error)
    {
        for (i = memoria->RAM[indice + CS]; i < memoria->RAM[indice + DS]; i++)
            memoria->RAM[i] = aux.RAM[i - memoria->RAM[indice + CS]];
    }
    return error;
}

int CargarImagenes(TMemoria *memoria, TEntrada entrada)
{
    int i = 0, cantImagenes = entrada.cantidadImagenes, error = 0;
    TMemoria memoriaAuxiliar;

    memoria->RAM[0] = cantImagenes;
    memoria->RAM[1] = 0;

    while (i < cantImagenes && error == 0)
    {
        CargarImagen(&memoriaAuxiliar, entrada.direccionesImagenes[i]);
        error = PasarAMemoria(memoria, memoriaAuxiliar, cantImagenes, i);

        i++;
    }
    return error;
}
//////////////////////////////////////////////////////////////////////////////////////////
void MostrarFlagsA(TFlags flags, TMemoria memoria)
{
    int i, pos = 2;

    if (flags[0])
    {
        printf("\nCantidad total de procesos = %d", memoria.RAM[0]);
        printf("\nCantidad de procesos finalizados correctamente = %d", memoria.RAM[1]);

        for (i = 0; i < memoria.RAM[1]; i++)
        {
            printf("\n\nProceso %d", i);
            printf("\nPS = %10d | CS = %10d | DS = %10d | ES = %10d |", memoria.RAM[pos + PS], memoria.RAM[pos + CS], memoria.RAM[pos + DS], memoria.RAM[pos + ES]);
            printf("\nIP = %10d | SS = %10d | SP = %10d | BP = %10d |", memoria.RAM[pos + IP], memoria.RAM[pos + SS], memoria.RAM[pos + SP], memoria.RAM[pos + BP]);
            printf("\nAC = %10d | CC = %10d | AX = %10d | BX = %10d |", memoria.RAM[pos + AC], memoria.RAM[pos + CC], memoria.RAM[pos + AX], memoria.RAM[pos + BX]);
            printf("\nCX = %10d | DX = %10d | EX = %10d | FX = %10d |", memoria.RAM[pos + CX], memoria.RAM[pos + DX], memoria.RAM[pos + EX], memoria.RAM[pos + FX]);

            pos += TAM_REG;
        }
    }
}
void MostrarFlagsB(TFlags flags, TMemoria memoria)
{
    char cadena[20], *num;
    int i, cant, direccion[2] = { 0 }, condicion = 0, contenido;

    if (flags[1])
    {
        do
        {
            printf("\n[%03d] cmd: ", getLinea(memoria.REG[IP]));
            fflush(stdin);
            gets(cadena);
            num = strtok(cadena, " ");
            cant = 0;
            while( num != NULL && condicion == 0)
            {
                direccion[cant] = atoi(num);
                if (direccion[cant] < 0 || cant >= 2)
                    condicion = 1;
                else
                {
                    cant++;
                    num = strtok(NULL, " ");
                }
            }
            if (!condicion && cant > 0)
            {
                direccion[1] = (cant == 1)? 1: (direccion[1] - direccion[0] + 1);
                for (i = 0; i < direccion[1]; i++)
                {
                    contenido = memoria.RAM[direccion[0] + i];
                    printf("\n[%04d]: ", (direccion[0] + i));
                    MostrarConEspacio(16, contenido, 4, 8);
                    printf(" %c %3d", getASCII(contenido), contenido);
                }
            }
        }while (strlen(cadena) > 0);
    }
}
void MostrarFlagsC(TFlags flags)
{
    if (flags[2])
    {
        system("cls");
    }
}
void MostrarFlagsD(TFlags flags, TMemoria memoria, int marcar)
{
    if (flags[3])
    {
        MostrarCodigoAssembler(memoria, marcar);
    }
}
//////////////////////////////////////////////////////////////////////////////////////////

int InterpretarInstruccion(int celda)
{
    return celda >> 16;
}

void ObtenerInstruccion(TMemoria memoria, int i, int *codInstruccion, int *codOperando1, int *codOperando2)
{
    (*codInstruccion) = memoria.RAM[i];
    (*codOperando1) = memoria.RAM[i+1];
    (*codOperando2) = memoria.RAM[i+2];
}
int CalcularCantidadDigitos(int base, int num)
{
    int i = 0;
    if (num == 0)
        i++;
    else
        while (num > 0)
        {
            num/=base;
            i++;
        }

    return i;
}
char *getFormato(int base)
{
    switch (base)
    {
        case 8: return "%o";
        case 16: return "%x";
    }
    return "%d";
}
//MUESTRA POR PANTALLA UN NUMERO PARTIDO POR UN ESPACIO
void MostrarConEspacio(int base, int num, int posicionEspacio, int cantDigitos)
{
    char *formato = getFormato(base);
    char numMostrar[cantDigitos + 1], cadena[cantDigitos + 1];
    int i, j, k, cant;

    sprintf(cadena, formato, num);
    cant = strlen(cadena);

    for (k = 0, i = 0, j = 0; i < cantDigitos + 1; i++)
    {
        if (i == posicionEspacio)
            numMostrar[i] = ' ';
        else
        {
            numMostrar[i] = (cantDigitos - k > cant)? '0':cadena[j++];
            k++;
        }
    }
    numMostrar[i] = '\0';
    printf("%s", strupr(numMostrar));
}
//MOSTRAR DIRECCION
void MostrarDireccion(int base, int numero, int espacio, int cantDigitos)
{
    printf("[");
    MostrarConEspacio(base, numero, espacio, cantDigitos);
    printf("]: ");
}
//MOSTRAR CODIGO ASSEMBLER
void MostrarArgumento(int primerArgumento, int tOper, int oper)
{
    char argumento[25];
    switch (tOper)
    {
    case 0: //INMEDIATO
        snprintf(argumento, sizeof(argumento), "%d", oper);
        break;
    case 1: //DE REGISTRO
        snprintf(argumento, sizeof(argumento), "%s", getNombreDelRegistro(oper&0x0000000f));
        break;
    case 2: //DIRECTO
        snprintf(argumento, sizeof(argumento), "[%s:%d]", getNombreDelRegistro((oper&0xf0000000) >> 28), oper&0x0fffffff);
        break;
    case 3: //INDIRECTO
        snprintf(argumento, sizeof(argumento),"[%s:%s", getNombreDelRegistro((oper&0xf0000000)>>28), getNombreDelRegistro(oper&0x0000000f));
        if ((oper&0x0ffffff0)>>4 != 0)
        {
            if ((oper & 0x0f000000) == 0)
                snprintf(argumento, sizeof(argumento), "%s%c", argumento, '+');
            snprintf(argumento, sizeof(argumento), "%s%d", argumento, (oper << 4) >> 8);
        }
        snprintf(argumento, sizeof(argumento), "%s]", argumento);

        break;
    }
    if (!primerArgumento)
        printf("%11s", argumento);
    else
        printf(", %s", argumento);
}
int MnemonicoCantArgumentos(int codMnemonico)
{
    switch (codMnemonico)
    {
        case 0x20:
        case 0x24:
        case 0x25:
        case 0x26:
        case 0x27:
        case 0x28:
        case 0x29:
        case 0x33:
        case 0x40:
        case 0x44:
        case 0x45:
        case 0x81:
            return 1;
        case 0x8f:
        case 0x48:
            return 0;
        default: return 2;
    }
}
void MostrarCodigoAssembler(TMemoria memoria, int marcar)
{
    char *StringMnemonicos[256];
    int i = memoria.REG[CS], codInstruccion, oper1, oper2, codMnemonico, cantArgumentos;

    CargarStringMnemonicos(StringMnemonicos);

    printf("Codigo:\n");

    while (i < memoria.REG[DS] && (memoria.RAM[i] & 0xffffff00) != 0x00000000)
    {
        ObtenerInstruccion(memoria, i, &codInstruccion, &oper1, &oper2);
        codMnemonico = InterpretarInstruccion(codInstruccion);

        if (marcar && i == getIP(memoria))
            printf(">");
        else
            printf(" ");

        MostrarDireccion(16, i, 4, 8);

        //MUESTRA CODIDGO DE INSTRUCCION
        printf("%04X ", codInstruccion >> 16);
        //MUESTRA TIPOS DE OPERANDOS
        printf("%04X ", codInstruccion & 0x0000ffff);

        //MUESTRA LOS OPERANDOS
        printf("%04X ", oper1 >> 16);
        printf("%04X ", oper1 & 0x0000ffff);

        printf("%04X ", (oper2>>16)&0x0000ffff);
        printf("%04X ", oper2 & 0x0000ffff);

        printf("\t%3d: ", getLinea(i - memoria.REG[CS]));

        printf("\t%s ", StringMnemonicos[codMnemonico]);

        cantArgumentos = MnemonicoCantArgumentos(codMnemonico);

        if (cantArgumentos > 0)
        {
            MostrarArgumento(0, (memoria.RAM[i]&0x0000ff00) >> 8, oper1);
            if (cantArgumentos > 1)
            {
                MostrarArgumento(1, (memoria.RAM[i]&0x000000ff), oper2);
            }
        }

        printf("\n");
        i+=3;
    }
}

//////////////////////////////////////////////////////////////////////////////////////////

//DEFINE EL TIPO DE OPERANDO (DIRECTO, DE REGISTRO O IMNEDIATO) EN "TIPO" Y ASIGNA SU VALOR EN "OPERANDO"
void tipoOperandos(TMemoria memoria, int celda, int *tipo, int *operando)
{
    (*tipo) = celda;
    switch (celda)
    {
        case 0x00: //OPERANDO INMEDIATO
        break;
        case 0x01: //OPERANDO DE REGISTRO
            (*operando) &= 0x0000000f;
        break;
        case 0x02: //OPERANDO DIRECTO
            (*operando) = memoria.REG[((*operando)&0xf0000000) >> 28] + ((*operando)&0x0fffffff);
        break;
        case 0x03: //OPERANDO INDIRECTO
            (*operando) = memoria.REG[((*operando)&0xf0000000) >> 28] + memoria.REG[(*operando)&0x0000000f] + (((*operando) << 4) >> 8);
        break;
    }
}
//PREPARA LA INSTRUCCIONES PARA OBTENER LOS TIPOS Y OPERANDOS
void ObtenerOperandos(TMemoria memoria, int Instancia, int *tOper1, int *Oper1, int *tOper2, int *Oper2)
{
    int inst1 = (Instancia&0x0000ff00) >> 8, inst2 = Instancia&0x000000ff;

    tipoOperandos(memoria, inst1, tOper1, Oper1);
    tipoOperandos(memoria, inst2, tOper2, Oper2);
}
//EJECUTA LA INSTRUCCION, A PARTIR DEL TIPO DE OPERANDOS. SACA LOS VALORES DEL REGISTRO, RAM O DIRECTAMENTE. LUEGO SI ES EL CASO
//LOS GUARDA EN DONDE CORRESPONDA (RAM O REGISTRO)
int EjecutarInstruccion(TMemoria *memoria, T_FUNC* mnemonicos, TFlags flags, int tMnemonico, int tOper1, int Oper1, int tOper2, int Oper2)
{
    int *arg1, *arg2, auxIP = getIP(*memoria), error;

    switch (tOper1)
    {
        case 0: arg1 = &Oper1; break; //INMEDIATO
        case 1: arg1 = &memoria->REG[Oper1]; break; //REGISTRO
        case 2: arg1 = &memoria->RAM[Oper1]; break;
        case 3: arg1 = &memoria->RAM[Oper1];
    }
    switch (tOper2)
    {
        case 0: arg2 = &Oper2; break;
        case 1: arg2 = &memoria->REG[Oper2]; break;
        case 2: arg2 = &memoria->RAM[Oper2]; break; //DIRECTO y INDIRECTO
        case 3: arg2 = &memoria->RAM[Oper2];
    }

    error = mnemonicos[tMnemonico](memoria, flags, arg1, arg2);

    if (error == 0 && auxIP == getIP(*memoria))
    {
        memoria->REG[IP]+=3;
    }
    return error;
}
//EJECUTA EL CODIGO A PARTIR DE LOS DATOS DE LA RAM Y EL REG
int EjecutarPrograma(TMemoria *memoria, T_FUNC mnemonicos[TAM_MEMONICOS], TFlags flags)
{
    int tipoMnemonico, codInstruccion, tipoOperando1, tipoOperando2, codOperando1, codOperando2, error = 0;

    MostrarFlagsD(flags, *memoria, 0);

    while (error == 0 && getIP(*memoria) < memoria->REG[DS] && (memoria->RAM[getIP(*memoria)] & 0xffffff00) != 0x00000000)
    {
        ObtenerInstruccion(*memoria, getIP(*memoria), &codInstruccion, &codOperando1, &codOperando2);
        ObtenerOperandos(*memoria, codInstruccion, &tipoOperando1, &codOperando1, &tipoOperando2, &codOperando2);
        tipoMnemonico = InterpretarInstruccion(codInstruccion);
        error = EjecutarInstruccion(memoria, mnemonicos, flags, tipoMnemonico, tipoOperando1, codOperando1, tipoOperando2, codOperando2);
    }

    return error;
}
void MostrarErrores(int error)
{
    switch (error)
    {
        case ERROR_STACK_OVERFLOW : printf("STACK OVERFLOW!"); break;
        case ERROR_STACK_UNDERFLOW: printf("STACK UNDERFLOW!"); break;
    }
}
void EjecutarMemoria(TMemoria *memoria, TFlags flags)
{
    int error = 0;
    T_FUNC mnemonicos[TAM_MEMONICOS];

    CargarMnemonicos(mnemonicos);
    MostrarFlagsC(flags);

    while (error == 0 && memoria->RAM[1] < memoria->RAM[0])
    {
        CopiarRegistros(memoria->REG, 0, memoria->RAM, 2 + TAM_REG * memoria->RAM[1]);
        error = EjecutarPrograma(memoria, mnemonicos, flags);
        CopiarRegistros(memoria->RAM, 2 + TAM_REG * memoria->RAM[1], memoria->REG, 0);

        memoria->RAM[1]++;
        printf("\n");
    }
    MostrarErrores(error);
    MostrarFlagsA(flags, *memoria);
}

//----------------------------------------------------------------
int getIP(TMemoria memoria)
{
    return memoria.REG[IP] + memoria.REG[CS];
}
void setIP(TMemoria *memoria, int ip)
{
    memoria->REG[IP] = ip;
}
char getASCII(int num)
{
    char caracter = num;
    if (num < 32 || num >= 255)
        caracter = '.';
    return caracter;
}
int getLinea(int num)
{
    return num / 3 + 1;
}
char *getNombreDelRegistro(int i)
{
    switch (i)
    {
        case 0x0: return "PS";
        case 0x1: return "CS";
        case 0x2: return "DS";
        case 0x3: return "ES";
        case 0x4: return "IP";
        case 0x5: return "SS";
        case 0x6: return "SP";
        case 0x7: return "BP";
        case 0x8: return "AC";
        case 0x9: return "CC";
        case 0xa: return "AX";
        case 0xb: return "BX";
        case 0xc: return "CX";
        case 0xd: return "DX";
        case 0xe: return "EX";
        case 0xf: return "FX";
    }
    return '\0';
}
//----------------------------------------------------------------
//CARGA LOS STRINGS DE LOS MNEMONICOS PARA MOSTRARLOS POR PANTALLA
void CargarStringMnemonicos(char *StringMmemonicos[])
{
    StringMmemonicos[0x1]  = "MOV ";
    StringMmemonicos[0x2]  = "ADD ";
    StringMmemonicos[0x3]  = "SUB ";
    StringMmemonicos[0x4]  = "MUL ";
    StringMmemonicos[0x5]  = "DIV ";
    StringMmemonicos[0x6]  = "MOD ";
    StringMmemonicos[0x13] = "CMP ";
    StringMmemonicos[0x17] = "SWAP";
    StringMmemonicos[0x19] = "RND ";
    StringMmemonicos[0x31] = "AND ";
    StringMmemonicos[0x32] = "OR  ";
    StringMmemonicos[0x33] = "NOT ";
    StringMmemonicos[0x34] = "XOR ";
    StringMmemonicos[0x37] = "SHL ";
    StringMmemonicos[0x38] = "SHR ";
    StringMmemonicos[0x20] = "JMP ";
    StringMmemonicos[0x21] = "JE  ";
    StringMmemonicos[0x22] = "JG  ";
    StringMmemonicos[0x23] = "JL  ";
    StringMmemonicos[0x24] = "JZ  ";
    StringMmemonicos[0x25] = "JP  ";
    StringMmemonicos[0x26] = "JN  ";
    StringMmemonicos[0x27] = "JNZ ";
    StringMmemonicos[0x28] = "JNP ";
    StringMmemonicos[0x29] = "JNN ";

    StringMmemonicos[0x40] = "CALL";
    StringMmemonicos[0x44] = "PUSH";
    StringMmemonicos[0x45] = "POP ";
    StringMmemonicos[0x48] = "RET ";

    StringMmemonicos[0x50] = "SLEN";
    StringMmemonicos[0x51] = "SMOV";
    StringMmemonicos[0x53] = "SCMP";

    StringMmemonicos[0x81] = "SYS ";
    StringMmemonicos[0x8f] = "STOP";
}
//CARGA EL VECTOR DE FUNCIONES
void CargarMnemonicos(T_FUNC *mnemonicos)
{
    mnemonicos[0x1] = &func_MOV;
    mnemonicos[0x2] = &func_ADD;
    mnemonicos[0x3] = &func_SUB;
    mnemonicos[0x4] = &func_MUL;
    mnemonicos[0x5] = &func_DIV;
    mnemonicos[0x6] = &func_MOD;
    mnemonicos[0x13] = &func_CMP;
    mnemonicos[0x17] = &func_SWAP;
    mnemonicos[0x19] = &func_RND;
    mnemonicos[0x31] = &func_AND;
    mnemonicos[0x32] = &func_OR;
    mnemonicos[0x33] = &func_NOT;
    mnemonicos[0x34] = &func_XOR;
    mnemonicos[0x37] = &func_SHL;
    mnemonicos[0x38] = &func_SHR;
    mnemonicos[0x20] = &func_JMP;
    mnemonicos[0x21] = &func_JE;
    mnemonicos[0x22] = &func_JG;
    mnemonicos[0x23] = &func_JL;
    mnemonicos[0x24] = &func_JZ;
    mnemonicos[0x25] = &func_JP;
    mnemonicos[0x26] = &func_JN;
    mnemonicos[0x27] = &func_JNZ;
    mnemonicos[0x28] = &func_JNP;
    mnemonicos[0x29] = &func_JNN;

    mnemonicos[0x40] = &func_CALL;
    mnemonicos[0x44] = &func_PUSH;
    mnemonicos[0x45] = &func_POP;
    mnemonicos[0x48] = &func_RET;

    mnemonicos[0x50] = &func_SLEN;
    mnemonicos[0x51] = &func_SMOV;
    mnemonicos[0x53] = &func_SCMP;

    mnemonicos[0x81] = &func_SYS;
    mnemonicos[0x8f] = &func_STOP;
}

void ModificarCC(TMemoria *memoria, int num)
{
    if (num == 0)
    {
        memoria->REG[CC] |= 0x0001;
    }
    else
    {
        if (num < 0)
        {
            memoria->REG[CC] |= 0x1000;
        }
        else
        {
            memoria->REG[CC] = 0x0;
        }
    }
}

int NumeroSaltoMemoria(int linea)
{
    return 3 * (linea - 1);
}

int func_MOV(TMemoria *memoria, TFlags flags, int *arg1, int *arg2)
{
    (*arg1) = (*arg2);

    return 0;
}
int func_ADD(TMemoria *memoria, TFlags flags, int *arg1, int *arg2)
{
    (*arg1)+=(*arg2);
    ModificarCC(memoria, *arg1);

    return 0;
}
int func_SUB(TMemoria *memoria, TFlags flags, int *arg1, int *arg2)
{
    (*arg1)-=(*arg2);
    ModificarCC(memoria, *arg1);

    return 0;
}
int func_MUL(TMemoria *memoria, TFlags flags, int *arg1, int *arg2)
{
    (*arg1)*=(*arg2);
    ModificarCC(memoria, *arg1);

    return 0;
}
int func_DIV(TMemoria *memoria, TFlags flags, int *arg1, int *arg2)
{
    (*arg1)/=(*arg2);
    ModificarCC(memoria, *arg1);

    return 0;
}
int func_MOD(TMemoria *memoria, TFlags flags, int *arg1, int *arg2)
{
    (*arg1) %= (*arg2);
    ModificarCC(memoria, (*arg1) / (*arg2));

    return 0;
}
int func_CMP(TMemoria *memoria, TFlags flags, int *arg1, int *arg2)
{
    ModificarCC(memoria, (*arg1) - (*arg2));

    return 0;
}
int func_SWAP(TMemoria *memoria, TFlags flags, int *arg1, int *arg2)
{
    int aux = (*arg1);
    (*arg1) = (*arg2);
    (*arg2) = aux;

    return 0;
}
int func_RND(TMemoria *memoria, TFlags flags, int *arg1, int *arg2)
{
    srand(time(0));
    (*arg1) = rand()%(*arg2);

    return 0;
}
int func_AND(TMemoria *memoria, TFlags flags, int *arg1, int *arg2)
{
    (*arg1) &= (*arg2);
    ModificarCC(memoria, *arg1);

    return 0;
}
int func_OR(TMemoria *memoria, TFlags flags, int *arg1, int *arg2)
{
    (*arg1) |= (*arg2);
    ModificarCC(memoria, *arg1);

    return 0;
}
int func_NOT(TMemoria *memoria, TFlags flags, int *arg1, int *arg2)
{
    (*arg1) = !(*arg1);
    ModificarCC(memoria, *arg1);

    return 0;
}
int func_XOR(TMemoria *memoria, TFlags flags, int *arg1, int *arg2)
{
    (*arg1) = ((*arg1) == (*arg2))? 0 : 1;
    ModificarCC(memoria, *arg1);

    return 0;
}
int func_SHL(TMemoria *memoria, TFlags flags, int *arg1, int *arg2)
{
    (*arg1) <<= (*arg2);
    ModificarCC(memoria, *arg1);

    return 0;
}
int func_SHR(TMemoria *memoria, TFlags flags, int *arg1, int *arg2)
{
    (*arg1) >>= (*arg2);
    ModificarCC(memoria, *arg1);

    return 0;
}
int func_JMP(TMemoria *memoria, TFlags flags, int *arg1, int *arg2)
{
    setIP(memoria, NumeroSaltoMemoria(*arg1));

    return 0;
}
int func_JE(TMemoria *memoria, TFlags flags, int *arg1, int *arg2)
{
    if ((*arg1) == memoria->REG[AX])
    {
        setIP(memoria, NumeroSaltoMemoria(*arg2));
    }

    return 0;
}
int func_JG(TMemoria *memoria, TFlags flags, int *arg1, int *arg2)
{
    if ((*arg1) > memoria->REG[AX])
    {
        setIP(memoria, NumeroSaltoMemoria(*arg2));
    }

    return 0;
}
int func_JL(TMemoria *memoria, TFlags flags, int *arg1, int *arg2)
{
    if ((*arg1) < memoria->REG[AX])
    {
        setIP(memoria, NumeroSaltoMemoria(*arg2));
    }

    return 0;
}
int func_JZ(TMemoria *memoria, TFlags flags, int *arg1, int *arg2)
{
    if ((memoria->REG[CC] & 0xfff1) == 0x0001)
    {
        setIP(memoria, NumeroSaltoMemoria(*arg1));
    }

    return 0;
}
int func_JP(TMemoria *memoria, TFlags flags, int *arg1, int *arg2)
{
    if ((memoria->REG[CC] & 0x1fff) == 0x0000)
    {
        setIP(memoria, NumeroSaltoMemoria(*arg1));
    }

    return 0;
}
int func_JN(TMemoria *memoria, TFlags flags, int *arg1, int *arg2)
{
    if ((memoria->REG[CC] & 0x1fff) == 0x1000)
    {
        setIP(memoria, NumeroSaltoMemoria(*arg1));
    }

    return 0;
}
int func_JNZ(TMemoria *memoria, TFlags flags, int *arg1, int *arg2)
{
    if ((memoria->REG[CC] & 0xfff1) == 0x0000)
    {
        setIP(memoria, NumeroSaltoMemoria(*arg1));
    }

    return 0;
}
int func_JNP(TMemoria *memoria, TFlags flags, int *arg1, int *arg2)
{
    if ((memoria->REG[CC] & 0x1fff) == 0x1000 || (memoria->REG[CC] & 0xfff1) == 0x0001)
    {
        setIP(memoria, NumeroSaltoMemoria(*arg1));
    }

    return 0;
}
int func_JNN(TMemoria *memoria, TFlags flags, int *arg1, int *arg2)
{
    if ((memoria->REG[CC] & 0x1fff) == 0x0000 || (memoria->REG[CC] & 0xfff1) == 0x0001)
    {
        setIP(memoria, NumeroSaltoMemoria(*arg1));
    }

    return 0;
}

//INTRUCCIONES DE PILA////////////////////////////////////////////////

int func_PUSH(TMemoria *memoria, TFlags flags, int *arg1, int *arg2)
{
    int error = 0;
    if (memoria->REG[SP] > 0)
    {
        --memoria->REG[SP];
        memoria->RAM[memoria->REG[SS] + memoria->REG[SP]] = (*arg1);
    }
    else
        error = ERROR_STACK_OVERFLOW; // Stack overflow

    return error;
}
int func_POP(TMemoria *memoria, TFlags flags, int *arg1, int *arg2)
{
    int error = 0;
    if (memoria->REG[SS] + memoria->REG[SP] < memoria->REG[CS] + memoria->REG[PS])
    {
        (*arg1) = memoria->RAM[memoria->REG[SS] + memoria->REG[SP]];
        memoria->REG[SP]++;
    }
    else
        error = ERROR_STACK_UNDERFLOW; // Stack underflow
    return error;
}
int func_CALL(TMemoria *memoria, TFlags flags, int *arg1, int *arg2)
{
    int error, aux = memoria->REG[IP];
    error = func_PUSH(memoria, flags, &aux, arg2);
    if (error == 0)
        setIP(memoria, NumeroSaltoMemoria(*arg1));

    return error;
}
int func_RET(TMemoria *memoria, TFlags flags, int *arg1, int *arg2)
{
    int error = func_POP(memoria, flags, arg1 , arg2);
    (*arg1)+=3;
    if (error == 0)
        setIP(memoria, (*arg1));

    return error;
}

///////////////////////////////////////////////////////////////////////

int func_SLEN(TMemoria *memoria, TFlags flags, int *arg1, int *arg2)
{
    int i = 0;
    while ((*arg2) != '\0')
    {
        i++;
        arg2++;
    }
    (*arg1) = i;

    return 0;
}
int func_SMOV(TMemoria *memoria, TFlags flags, int *arg1, int *arg2)
{
    while ((*arg2) != '\0')
    {
        (*arg1) = (*arg2);
        arg1++;
        arg2++;
    }

    return 0;
}
int func_SCMP(TMemoria *memoria, TFlags flags, int *arg1, int *arg2)
{
    int resultado;
    do
    {
        resultado = (*arg1) - (*arg2);
        arg1++;
        arg2++;
    }
    while (resultado == 0 && (*arg1) != '\0' && (*arg2) != '\0');

    ModificarCC(memoria, resultado);

    return 0;
}
int func_SYS(TMemoria *memoria, TFlags flags, int *arg1, int *arg2)
{
    char cadena[50];
    int configuracion = memoria->REG[AX], prompt = 0, endline = 0, caracter = 0, direccion, espacio = 0, i;

    switch ((*arg1))
    {
        case 0:
            {
                MostrarFlagsC(flags);
                MostrarFlagsD(flags, *memoria, 1);
                MostrarFlagsB(flags, *memoria);
            }
            break;
        case 1://READ
            {
                direccion = memoria->REG[DX] + memoria->REG[DS];

                if ((configuracion & 0xf000) == 0x0000)
                    prompt = 1;

                if ((configuracion & 0x0f00) == 0x0100)
                { // LEE STRING
                    if (prompt)
                    {
                        MostrarDireccion(10, memoria->REG[DX], -1, 4);
                    }
                    fflush(stdin);
                    gets(cadena);
                    for (i = 0; i < strlen(cadena); i++)
                    {
                        memoria->RAM[direccion + i] = (int)cadena[i];
                    }
                    memoria->RAM[direccion + i] = '\0';
                }
                else
                {
                    for (i = 0; i < memoria->REG[CX]; i++)
                    {
                        if (prompt)
                        {
                            MostrarDireccion(10, memoria->REG[DX] + i, -1, 4);
                        }
                        fflush(stdin);
                        if ((configuracion & 0x0008) == 0x0008)
                            scanf("%x", &memoria->RAM[direccion + i]);
                        if ((configuracion & 0x0004) == 0x0004)
                            scanf("%o", &memoria->RAM[direccion + i]);
                        if ((configuracion & 0x0001) == 0x0001)
                            scanf("%d", &memoria->RAM[direccion + i]);

                        if (endline)
                            printf("\n");
                    }
                }

            }
        break;
        case 2://WRITE
        case 3:
            {
                direccion = memoria->REG[DX] + memoria->REG[DS];

                if ((configuracion & 0xf000) == 0x0000)
                    prompt = 1;

                if ((configuracion & 0x0f00) == 0x0000)
                    endline = 1;

                if ((configuracion & 0x00f0) == 0x0010)
                    caracter = 1;

                if ((configuracion & 0x000f) != 0x0000)
                    espacio = 1;
                for (i = 0; i < memoria->REG[CX]; i++)
                {
                    if (prompt && (i == 0 || endline))
                        MostrarDireccion((endline)? 16:10, memoria->REG[DX] + i, -1, 4);

                    if (caracter)
                    {
                        printf("%c", getASCII(memoria->RAM[direccion + i]));
                        if (espacio)
                            printf(" ");
                    }

                    if ((configuracion & 0x0008) == 0x0008)
                        printf("%%%X ", memoria->RAM[direccion + i]);
                    if ((configuracion & 0x0004) == 0x0004)
                        printf("@%o ", memoria->RAM[direccion + i]);
                    if ((configuracion & 0x0001) == 0x0001)
                        printf("%d", memoria->RAM[direccion + i]);

                    if (endline)
                        printf("\n");
                }

                if ((int)(*arg1) == 3)//DUMP
                {
                    for (i = 0; i < TAM_REG; i++)
                    {
                        if (prompt && (i == 0 || endline))
                            printf("[%s]: ", getNombreDelRegistro(i));

                        if (caracter)
                            printf("%c ", getASCII(memoria->REG[i]));

                        if ((configuracion & 0x0008) == 0x0008)
                            printf("%%%04X ", memoria->REG[i]);
                        if ((configuracion & 0x0004) == 0x0004)
                            printf("@%04o ", memoria->REG[i]);
                        if ((configuracion & 0x0001) == 0x0001)
                            printf("%04d", memoria->REG[i]);
                        if (endline)
                            printf("\n");
                    }
                }
            }
        break;
        case 10:
            {
                direccion = memoria->REG[DX] + (memoria->REG[BX] == 2)? memoria->REG[DS]: memoria->REG[ES];

                if ((configuracion & 0xf000) == 0x0000)
                    prompt = 1;

                if (prompt)
                    MostrarDireccion(10, memoria->REG[DX], -1, 4);

                fflush(stdin);
                gets(cadena);

                for (i = 0; i < strlen(cadena); i++)
                {
                    memoria->RAM[direccion + i] = (int)cadena[i];
                }
                memoria->RAM[direccion + i] = '\0';
            }
            break;
        case 20:
            {
                direccion = memoria->REG[DX];
                switch (memoria->REG[BX])
                {
                    case 1: direccion+= memoria->REG[CS]; break;
                    case 2: direccion+= memoria->REG[DS]; break;
                    case 3: direccion+= memoria->REG[ES]; break;
                }

                if ((configuracion & 0xf000) == 0x0000)
                    prompt = 1;

                if ((configuracion & 0x0f00) == 0x0000)
                    endline = 1;

                if (prompt)
                    MostrarDireccion((endline)? 16:10, memoria->REG[DX], -1, 4);

                i = 0;
                while (memoria->RAM[direccion + i] != '\0')
                {
                     printf("%c", getASCII(memoria->RAM[direccion + i]));
                     i++;
                }

                if (endline)
                    printf("\n");
            }
            break;
    }

    return 0;
}
int func_STOP(TMemoria *memoria, TFlags flags, int *arg1, int *arg2)
{
    setIP(memoria, memoria->REG[DS]);

    return 0;
}
