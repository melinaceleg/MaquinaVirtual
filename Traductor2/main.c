#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>
#include <conio.h>
#define DS 2
#define ES 3
#define IP 4
#define AC 8
#define CC 9
#define AX 10
#define BX 11
#define CX 12
#define DX 13
#define EX 14
#define FX 15


typedef struct ///para asociar nombre con valor de los registros
{
    char nom[2];
    int valor;
} Registro;

typedef struct ///linea a guardar en IMG
{
    int codIns; ///32bits de instruccion pasado a entero
    int CODop1; ///cod operando1 32 bits
    int CODop2; ///cod operando2 32 bits
} tipoInst;


typedef struct ///estructura de rotulo
{
    char nom[15];
    int linea;
} Rotulo;

int comparaCodigos(char *pInstruccion, char mnem[][5])
{
    int enc=-1;
    int i=0x01;
    while ( i <= 0x8F && enc == -1)
    {

        if (strcmp(pInstruccion,mnem[i])== 0)
        {
            enc = i;
        }
        i= i + 0x01;
    }
    return enc;
}


int siEsRegistro(char *op, Registro registros[])
{
    int i = 0;
    int enc=-1;
    while (i < 16 && strcmp(op,registros[i].nom) != 0)
    {
        i++;
    }
    if (i<16)
        enc=i;
    return enc;
}

int siEsRotulo(char *op, Rotulo v[], int cantR)
{
    int i =0;
    int enc=-1;

    while (i< cantR && enc == -1 )
    {
        if (strcmp(op,v[i].nom)== 0)
            enc = i;

        i++;
    }
    return enc;
}


int devOperando(int *mal,int *tipo,char *op, Registro registros[], int indiceMNEM, Rotulo vecRotulos[], int cantR) ///modifico tipo y devuelvo los 32 bits codOP
{
    long int entero=0; ///para conversiones;
    int reg;
    char *trash;
    int codOP; /// representa 32 bits de codigo operando maquina
    if (op && op[0] != ' ' && op[0] != '\n' && op[0] != '\0')
    {
        reg=siEsRegistro(op,registros);
        if (reg != -1)
        {
            *tipo=1;
            codOP=reg; ///ultimos 4 bits

        }
        else
        {
            if (strchr(op,'[') && strchr(op,']')) ///directo
            {
                *tipo=2;
                char* opAux=strtok(op,"[ ]");
                if (strchr(opAux,':'))
                {
                    if(strstr(opAux,"ES"))
                    {
                        codOP = ES;
                    }
                    else if(strstr(opAux,"DS"))
                    {
                        codOP=DS;
                    }
                    opAux=opAux+3;
                    codOP = (codOP << 28);
                    codOP = codOP | atoi(opAux);
                }
                else
                {
                    if (isdigit(opAux[0]))
                    {
                        codOP= DS;
                        codOP = (codOP << 28) | atoi(opAux); ///expresión en 32 bits del registro
                    }
                    else
                    {
                        *mal=1;
                        codOP=0xFFFFFFFF;
                    }
                }
            }
            else
            {
                *tipo=0;
                if (strchr(op,39))
                {
                    entero = atoi(++op);
                    codOP = entero;
                }
                if (strchr(op,'@')) ///si es octal
                {
                    entero = strtoul(++op,&trash,8);
                    codOP= entero;
                }
                else if (strchr(op,'%')) ///si es hexa
                {
                    entero = strtoul(++op,&trash,16);
                    codOP= entero;
                }

                else if (strchr(op,'#') || isdigit((op)[0])) ///si es decimal
                {
                    if (strchr(op,'#'))
                        op++;

                    entero=atoi(op);
                    codOP= entero;
                }
                else if (((indiceMNEM >= 32 && indiceMNEM <=41) || indiceMNEM == 19) && isalpha(op[0])) ///si es un rotulo sólo para indices de mnemonico determinados
                {
                    int j;
                    j=siEsRotulo(op,vecRotulos,cantR);
                    if(j == -1 || vecRotulos[j].linea == -1)
                    {
                        *mal =1;
                        codOP= 0xFFFFFFFF;
                    }
                    else
                        codOP = vecRotulos[j].linea; ///guarda la celda en memoria donde se encuentra el rotulo
                }
                else
                {
                    *mal = 1;
                    codOP=0xFFFFFFFF;
                }

            }
        }

    }
    else
    {
        *tipo = 0;
        codOP=0;
    }
    return codOP;
}

void impresionLinea(tipoInst linea, char *LineaReal, int nLinea, int nCelda)
{
    int aux;
    aux = linea.codIns;
    int i = 1;
    if (linea.codIns != 0xFFFFFFFF)
        printf("[%04X %04X]: ",nCelda>>16,nCelda);
    else
        printf("[%04X %04X]: ",0xFFFF,0xFFFF);
    while ( i >= 0)
    {
        aux = linea.codIns >> 16*i & 0xFFFF;
        printf("%04X ",aux);
        i--;
    }
    i=1;
    while (i>=0)
    {
        aux=linea.CODop1 >> 16*i & 0xFFFF;
        printf("%04X ",aux);
        i--;
    }
    i=1;
    while (i>=0)
    {
        aux=linea.CODop2 >> 16*i & 0xFFFF;
        printf("%04X ",aux);
        i--;
    }
    if (linea.codIns != 0xFFFFFFFF)
        printf("%d: // %s",nLinea,LineaReal);
    printf("\n");

}




tipoInst corteDatos(Rotulo rotulos[], int cantR, char *pInstruccion, char mnem[][5], Registro registros[])
{
    int codOP1=0;  ///En caso de que no posea algun operando
    int codOP2=0;
    int tipo = 0;
    tipoInst ins;
    ins.codIns= 0;
    ins.CODop1= 0;
    ins.CODop2 = 0;
    int mal=0;
    int tipoOP1, tipoOP2;
    tipoOP1=0;
    tipoOP2=0;
    int indice=-1;
    char *operando1=NULL;
    char *operando2=NULL;
    char *Mnemonico;
    if (*pInstruccion != '\0')
    {
        Mnemonico=strtok(pInstruccion," \n");
        indice = comparaCodigos(Mnemonico,mnem);
        if (indice != -1)
        {
            operando1 = strtok(NULL," ,");
            if (operando1)
            operando2= strtok(NULL," \0");

            codOP1=devOperando(&mal,&tipo,operando1,registros,indice,rotulos,cantR); /// devuelve los codigos de operando 1 de 32 bits
            tipoOP1=tipo;
            codOP2=devOperando(&mal,&tipo,operando2,registros,indice,rotulos,cantR);/// devuelve los codigos de operando 2 de 32 bits
            tipoOP2=tipo;
        }
    }
    else
        pInstruccion=NULL;

    if (pInstruccion && indice != -1 && (mal ==0) && tipo != 0xFFFFFFFF) /// si no hay errores
    {
        ins.codIns= indice;
        ins.codIns = (ins.codIns<<16) | (tipoOP1 << 8) | tipoOP2;
        ins.CODop1 = codOP1;
        ins.CODop2 = codOP2;

    }
    else
    {
        ins.codIns = ins.codIns | 0xFFFFFFFF;
        ins.CODop1 = 0xFFFFFFFF;
        ins.CODop2 = 0xFFFFFFFF;
    }

    return ins;
}


int siRotuloRepetido(Rotulo v[], int cantR, char *nuevo)
{
    int i = 0;
    while (i < cantR && strcmp(nuevo,v[i].nom))
    {
        i++;
    }
    return (i < cantR);
}

///si se le da un tamaño invalido a otro segmento

int traduccion(char *asmNOM, int ram[2000], Rotulo vecRotulos[], char mnem[][5], Registro registros[], char comando,int cantR)
{
    tipoInst linea; ///Linea a imprimir y guardar en RAM
    char *assembler = (char*)malloc(sizeof(char)*128);
    int nLinea=0; ///Lineas de instrucción
    int nCelda=0; ///indice de celda de instruccion
    int copiarAIMG=0; ///si existe algun error no copiará a img.
    char *pRenglon = (char *)malloc(sizeof(char)*128); ///renglon leído
    char *pInstruccion=NULL;
    char *p=NULL;
    char *cadena=NULL;
    char *subOp = (char*)malloc(sizeof(char)*128); ///para cortes
    FILE *archASM = fopen(asmNOM,"rt");
    char dato;
    int k=0;

    if (archASM)
    {
        dato = fgetc(archASM);
        while (!feof(archASM))
        {
            while(dato != '\n' && dato != EOF)
            {
                assembler[k]=dato;
                dato=fgetc(archASM);
                k++;
            }
            assembler[k]= '\0';
            k=0;


            if (assembler[0] != '\0' && assembler[0] != '\n') ///No incrementa linea y pasa
            {
                copiarAIMG=1;
                strcpy(pRenglon,assembler);
                cadena=strtok(pRenglon,"\t\n");
                strupr(cadena); ///PONE EN MAYUSCULAS TODA LA CADENA
                if (cadena[0] == '/')  ///SI ES SOLO UN COMENTARIO NO INCREMENTA LINEA
                {
                    cadena=strstr(cadena,"//");
                    printf("//%s\n", cadena);
                }
                else
                {
           //aca         if (strchr(cadena,'[') && strchr(cadena,':')) ///PUEDE HABER ROTULO Y OPERANDO DIRECTO
                    {
                        strcpy(subOp,cadena);
                        subOp = strtok(subOp,":");

                        if (strchr(subOp,'[')) ///NO TIENE ROTULO, TIENE SOLO OPERANDO
                            pInstruccion=cadena;
                        else
                        {
                            pInstruccion=strtok(NULL,"\n\0");

                        }

                    }
                    else
                    {
                        if (strchr(cadena,':'))
                        {
                            p=strtok(cadena,":");
                            pInstruccion=strtok(NULL,"\n\0");
                        }
                        else
                            pInstruccion=cadena;
                    }

                    if ((p=strchr(pInstruccion,'/')))
                    {
                        *p= '\0';
                    }

                    ///elimino espacios antes del mnemonico
                    while (*pInstruccion == ' ')
                        pInstruccion++;

                    linea= corteDatos(vecRotulos,cantR,pInstruccion,mnem,registros);

                    if (linea.codIns != 0xFFFFFFFF)
                    {
                        nLinea++;
                        ram[nCelda] = linea.codIns;
                        ram[nCelda+1] = linea.CODop1;
                        ram[nCelda+2] = linea.CODop2;
                        if (comando != 'o')
                            impresionLinea(linea,assembler,nLinea,nCelda);
                        nCelda +=3;

                    }
                    else
                    {
                        copiarAIMG=0;
                        if (comando != 'o')
                            impresionLinea(linea,assembler,nLinea,nCelda);
                    }
                }
            }

            cadena=NULL;
            pInstruccion=NULL;
            p=NULL;
            dato = fgetc(archASM);
        }
        if (nCelda>0)
            registros[DS].valor=nCelda-2; ///guarda el valor de DS QUE APUNTA AL DATA SEGMENT
    fclose(archASM);
    }
    else
        printf("ERROR EN ARCHIVO ASM\n");
    return copiarAIMG;
}

void copiaraIMG(int RAM[2000], Registro reg[], char *nomIMG)
{
    FILE *archIMG=fopen(nomIMG,"wb");
    int i=0;
    if (archIMG)
    {
        while (i < 16)
        {
            fwrite(&(reg[i].valor),sizeof(int),1,archIMG);
            i++;
        }
        fwrite(RAM,sizeof(int),2000,archIMG);
        fclose(archIMG);
    }
    else
        printf("ERROR EN ARCHIVO IMAGEN\n");

}



int main(int argc, char *argv[])
{
//    int reg[16];
    int ram[2000] = {0};
    char mnem[144][5];
    int c; ///determina si se copia a img o no
    Rotulo vecRotulos[10];
    Registro registros[16];
    inicRegistros(registros);
    inicCodigos(mnem);

//    char asmNOM[] = "archASM.asm";
    char *asmNOM;
    char *imgNOM;
    char comando=' ';
    int cantR;
    //traduccion(asmNOM,reg,ram,vecRotulos,mnem,registros,comando,cantR);


    for (int i = 0 ; i< argc; i++)
    {
        if (strstr(argv[i], ".asm"))
        {
            asmNOM= (char *)malloc(strlen(argv[i])*sizeof(char));
            asmNOM = argv[i];
            cantR=lecturaRotulos(asmNOM,vecRotulos);

        }
        if (strstr(argv[i], "-"))
        {
            comando = argv[i+1];
        }
        if (strstr(argv[i], ".img"))
        {
            imgNOM = (char*)malloc(strlen(argv[i])*sizeof(char));
            imgNOM = argv[i];
        }

    }
    c=traduccion(asmNOM,ram,vecRotulos,mnem,registros,comando,cantR); ///funcion principal que desglosa la traduccion
    if (c) ///Si es 0 no se grabara la imagen
        copiaraIMG(ram,registros,imgNOM);

    return 0;
}
