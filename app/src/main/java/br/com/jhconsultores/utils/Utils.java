package br.com.jhconsultores.utils;

//=============================================================================
//                           Biblioteca Android
//=============================================================================

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

/*
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.provider.MediaStore;
import android.provider.Settings;
 */

//=============================================================================
//                           Biblioteca Java
//=============================================================================
import static br.com.jhconsultores.sudoku.ui.MainActivity.cTAG;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/*
import java.io.BufferedOutputStream;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.OutputStream;
import java.net.URI;

import br.com.jhconsultores.sudoku.BuildConfig;
import br.com.jhconsultores.sudoku.ui.MainActivity;
 */


//=============================================================================
//                           Biblioteca JH
//=============================================================================
import br.com.jhconsultores.sudoku.R;

/*==============================================================================
 * Sistemas de permiss??o do Android 6.0
 * http://developer.android.com/preview/features/runtime-permissions.html
 *============================================================================*/
public class Utils {

    public String permScopedStorage = "";

    //--- Intancializa????es e inicializa????es
    private final String TAG_Utils = "Utils";
    String strLog = "";

    public Boolean flagSO_A11 = false;

    //--------------------------------------------------------------------------
    //                             Permissions
    //--------------------------------------------------------------------------
    //--------------------------------------------------------------------------
    // Verifica as permiss??es declaradas no AndroidManifest.xml
    // Livro: Google Android - pag 892 - cap 33
    //--------------------------------------------------------------------------
    public boolean VerificaPermissoes(Activity activity) {

        String[] permissoes;

        //--- O acesso aos arquivos da mem??ria externa em Android >= A11 (R) API30 (Scoped Storage)
        // ?? diferente do que o acesso em Android < A11 (Legacy Storage).

        // SO < A11
        if (!flagSO_A11) {

            permissoes = new String[]{ //Manifest.permission.RECORD_AUDIO,         // 11/01/21

                    Manifest.permission.INTERNET,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE};

        }
        // SO >= A11
        else {

            permissoes = new String[] { //Manifest.permission.RECORD_AUDIO,         // 11/01/21

                    Manifest.permission.INTERNET,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.MANAGE_EXTERNAL_STORAGE};

        }

        //------------------------------------------------------------------------
        boolean flagValidatePerm = validate(activity, 0, permissoes);
        //------------------------------------------------------------------------
        Log.d(cTAG, "-> Ret 'validate': " + ((flagValidatePerm)? "true":"false"));

        return (flagValidatePerm);

    }

    //-------------------------------------------------------------------------
    //  O SO pergunta ao usu??rio se autoriza-o a solicitar as permiss??es
    //-------------------------------------------------------------------------
    @RequiresApi(api = Build.VERSION_CODES.N)
    boolean validate(Activity activity, int requestCode, String... permissions) {

        boolean flagValidateOk = false;
        List<String> list = new ArrayList<String>();

        Log.d(cTAG, "   - Checa se permiss??es Ok");

        //--- Prepara a lista de permissions ungranted
        for (String permission : permissions) {

            // Valida permiss??o
            //---------------------------------------------------------------------------
            boolean ok = ContextCompat.checkSelfPermission(activity, permission) ==
                    PackageManager.PERMISSION_GRANTED;
            //---------------------------------------------------------------------------
            if (!ok) { list.add(permission); }

            Log.d(cTAG, "     - " + permission + ": " + ((ok) ? "true" : "false"));

        }

        //--- Se todas as permissions estiverem granted retorna com true
        if (list.isEmpty()) { flagValidateOk = true; }

        //--- Se houver 1+ permissions ungranted: requer permission
        else {

            Log.d(cTAG, "-> Recurso SEM Permiss??o atual:");

            //--- Se tiver uma chamada para Scoped Storage, separa-a para outro comando
            // https://stackoverflow.com/questions/65876736/how-do-you-request-manage-external-storage-permission-in-android
            permScopedStorage = "";
            for (String strPerm : list) {

                Log.d(cTAG, "   - " + strPerm);
                if (strPerm.equals("android.permission.MANAGE_EXTERNAL_STORAGE")) {

                    permScopedStorage = "android.permission.MANAGE_EXTERNAL_STORAGE";
                    list.removeIf(it -> it.equals("android.permission.MANAGE_EXTERNAL_STORAGE"));
                    break;

                }

            }

        }

        //--- Requer permiss??o para os recursos necess??rios para A10- ainda n??o permitidos
        if (!list.isEmpty()) {

            String[] newPermissions = new String[list.size()];
            list.toArray(newPermissions);

            Log.d(cTAG, "-> SO solicita autoriza????o para permiss??o:");
            for (String strNewPerm : newPermissions) {
                Log.d(cTAG, "   - " + strNewPerm);
            }
            //--------------------------------------------------------------------------
            ActivityCompat.requestPermissions(activity, newPermissions, 1);
            //--------------------------------------------------------------------------
        }

        return flagValidateOk;

    }

    //--------------------------------------------------------------------------
    //            ExtMem files m??todos (/storage/emulated/0/Download)
    //--------------------------------------------------------------------------
    //--------------------------------------------------------------------------
    // M??todo para listar arquivos em um diret??rio
    //--------------------------------------------------------------------------
    public String[] listaExtMemArqDir(String strDirName) {

        Log.d(TAG_Utils, "--> Lista arquivos do diretorio");
        String[] children   = new String[] {""};

        // String rootPath     = Environment.getExternalStorageDirectory().toString();
        // rootpath: "/storage/emulated/0"
        // String rootDirName  = rootPath + "/" + strDirName;
        // File file = new File("Download/sudoku/jogos");

        File fpath         = Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS);
        String rootDirName = fpath + "/" + strDirName;   // "sudoku/jogos";
        File file           = new File(rootDirName);

        try {

            // https://stackoverflow.com/questions/203030/best-way-to-list-files-in-java-sorted-by-date-modified
            // 1- obtain the array of (file, timestamp) pairs;
            int intIdx = 0;
            //---------------------------------
            File[] files = file.listFiles();
            //---------------------------------

            if (files == null) {

                Log.d(TAG_Utils, "   - File[] = null !");

            }

            else {

                Pair[] pairs = new Pair[1];

                if (files.length > 1) {
                    for (File file_ : files) {

                        Log.d(TAG_Utils, String.valueOf(intIdx++) + " " + file_.toString());

                    }
                    pairs = new Pair[files.length];
                    for (int i = 0; i < files.length; i++)
                        pairs[i] = new Pair(files[i]);

                    // 2- sort them by timestamp;
                    //------------------------------------------------
                    Arrays.sort(pairs, Collections.reverseOrder());
                    //------------------------------------------------

                    // 3- take the sorted pairs and extract only the file part, discarding the timestamp.
                    children = new String[files.length];
                    for (int i = 0; i < files.length; i++) {

                        files[i] = pairs[i].f;
                        children[i] = files[i].getName();

                    }

                } else if (files.length == 1) {

                    //files[0] = pairs[0].f;
                    children[0] = files[0].getName();

                }

                intIdx = 0;
                for (String child : children) {

                    Log.d(TAG_Utils, String.valueOf(intIdx++) + " " + child.toString());

                }
            }

        } catch (Exception exc) {

            Log.d(TAG_Utils, "Erro: " + exc.toString());

        }

        return children;

    }

    //--------------------------------------------------------------------------
    // M??todo para escrita de um arquivo
    //--------------------------------------------------------------------------
    public boolean escExtMemTextFile(Context context, String strPath, String strFileName, String strConteudo) {

        boolean flagEsc = false;
        File fpath      = null;
        File file       = null;
        File myFile     = null;
        String[] files  = null;

        //https://stackoverflow.com/questions/19853401/saving-to-sd-card-as-text-file
        // ScopedStorage
        try {

            fpath = Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS);

            String[] strPathsDir = strPath.split("/");

            Boolean flagDirOk = false;
            String strFilePath = fpath.getPath();
            for (String strPathDir : strPathsDir) {

                if (!strPathDir.isEmpty()) {

                    strFilePath += "/" + strPathDir;
                    file = new File(strFilePath);

                    if (!file.exists() || !file.isDirectory()) {

                        flagDirOk = file.mkdir();
                        if (!flagDirOk) break;

                    } else flagDirOk = true;
                }
            }

            //--- Se estiver num diret??rio:
            if (flagDirOk) {

                myFile = new File(strFilePath, ("/" + strFileName));
                myFile.createNewFile();

                //--- SO < A10
                //if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {

                    FileOutputStream fOut = new FileOutputStream(myFile);
                    OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut, Charset.forName("UTF-8"));

                    myOutWriter.append(strConteudo);
                    myOutWriter.close();
                    fOut.close();

                //}

                //--- SO >= A10
                /*
                else {

                    //----------------------------------------------------------------------------------
                    // Prepara o arquivo
                    String rootDirName = fpath + strPath + "/";   // "sudoku/jogos";

                    //----------------------------------------------------------------------------------
                    // Salvamento do arquivo
                    //----------------------------------------------------------------------------------
                    //--- Define o URI do conte??do a ser salvo
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.MediaColumns.DISPLAY_NAME,  strFileName);
                    values.put(MediaStore.MediaColumns.RELATIVE_PATH, rootDirName);  //Environment.DIRECTORY_DOWNLOADS);

                    ContentResolver contentResolver = context.getContentResolver();
                    Uri uri =
                          contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);

                    //--- Salva o conte??do no arquivo strFileName
                    if (uri != null) {

                        OutputStream outputStream = contentResolver.openOutputStream(uri);
                        if (outputStream != null) {

                            BufferedOutputStream bos = new BufferedOutputStream(outputStream);
                            byte[] arByBuffer        = strConteudo.getBytes();

                            strLog = arByBuffer.toString();
                            Log.d(cTAG, strLog);

                            bos.write(arByBuffer);
                            bos.flush();
                            bos.close();

                        }

                    }

                }
                 */
            }
            flagEsc = true;

        } catch (Exception exc) {

            Log.d(cTAG, "-> " + exc.getMessage());

        }

        return  flagEsc;

    }

    //--------------------------------------------------------------------------
    // M??todo para leitura de um arquivo
    //--------------------------------------------------------------------------
    //public ArrayList <String> LeituraTextFile(String enuModulo, String strFileName) {
    public ArrayList <String> leitExtMemTextFile(String strFileName) {

        //--- Instancializa????es e inicializa????es
        ArrayList<String> strArLstLeit = new ArrayList<>();
        File fpath         = null;
        File file          = null;
        FileInputStream in = null;

        //--- Leitura do arquivo
        boolean flagLogLocal = false;
        try {

            fpath = Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS);
            file = new File(fpath, strFileName);

            // https://stackoverflow.com/questions/12421814/how-can-i-read-a-text-file-in-android - coment??rios: Sandip
            in                      = new FileInputStream(file);
            BufferedReader myReader = new BufferedReader(new InputStreamReader(in, Charset.forName("UTF-8")));

            if (myReader.toString().length() < 1) {

                strLog = "Erro na leitura de: " + strFileName;
                Log.d(TAG_Utils, strLog);
                strArLstLeit = new ArrayList<>();

            } else {

                String aDataRow = "";
                while ((aDataRow = myReader.readLine()) != null) { strArLstLeit.add(aDataRow); }
                myReader.close();
                in.close();

            }

        } catch (Exception exc) {

            strArLstLeit = new ArrayList<>();
            Log.d(TAG_Utils, "Erro na leitura de: " + strFileName);
            Log.d(TAG_Utils, "Exc: " + exc.getMessage());

        }

        //--- Retorna
        return (strArLstLeit);
    }

    //--------------------------------------------------------------------------
    // M??todo para deletar o arquivo em um diretorio cujos nomes s??o recebidos.
    //--------------------------------------------------------------------------
     public boolean delExtMemFile(String strDirName, String strFileName) {
    //public boolean delExtMemFile(String strFileName) {

        boolean flagDelOk = false;

        /*
        Log.d(TAG_Utils, "--> Deleta Arquivo no diret??rio: " + strDirName);

        String rootDirName = Environment.getExternalStorageDirectory().toString() + strDirName;
        File Dir  = new File(rootDirName);
        File file = new File(rootDirName + strFileName);
        */

        File fpath = Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS);
        File Dir   = new File(fpath + "/" + strDirName);
        File file  = new File(Dir + "/", strFileName);

        Log.d(TAG_Utils, "--> Deleta: " + file);

        try {

            //--- Se o arquivo a ser deletado existir nesse diret??rio, deleta-o
            if (Dir.isDirectory()) {

                String[] children = Dir.list();

                int i = 0;
                for ( ; i < children.length; i++ ) {
                    if (children[i].equals(strFileName)) break;
                }

                if (i < children.length) {
                    //---------------------------
                    flagDelOk = file.delete();
                    //---------------------------
                }
            }

        } catch (Exception exc) {

            Log.d(TAG_Utils, "--> Erro: " + exc.getMessage());
            flagDelOk = false;

        }

        return flagDelOk;
    }

    //--------------------------------------------------------------------------
    // M??todo para deletar TODOS os arquivos de um diret??rio com nomes RegEx.
    //--------------------------------------------------------------------------
    public boolean delExtMemAllFiles(String strDirName, String [] strArRegEx) {

        Log.d(TAG_Utils, "--> Deleta Arquivos no Diret??rio " + strDirName);
        boolean flagDelOk = true;
        String rootDirName = Environment.getExternalStorageDirectory().toString() + strDirName;
        File Dir = new File(rootDirName);
        try {

            if (Dir.isDirectory()) {
                String[] children = Dir.list();
                for (int i = 0; i < children.length && flagDelOk; i++) {

                    boolean flagDelete = true;
                    for (int r = 0; r <  strArRegEx.length && flagDelete; r++) {

                        if (!children[i].contains(strArRegEx[r])) {

                            flagDelete = false;
                        }
                    }
                    if (flagDelete) {

                        flagDelOk = new File(Dir, children[i]).delete();

                    }
                }
            }

        } catch (Exception exc) {

            Log.d(TAG_Utils, "--> Erro: " + exc.getMessage());
            flagDelOk = false;
        }

        return flagDelOk;

    }

    //--------------------------------------------------------------------------
    // M??todo para deletar TODOS os arq e o Diret??rio (o delete ?? definitivo!)
    //--------------------------------------------------------------------------
    public boolean delExtMemDir(String strDirName) {

        Log.d(TAG_Utils, "--> Deleta Diret??rio " + strDirName);
        boolean flagDelOk = true;
        String rootDirName = Environment.getExternalStorageDirectory().toString() + strDirName;
        File Dir = new File(rootDirName);
        try {

            if (Dir.isDirectory()) {
                String[] children = Dir.list();
                for (int i = 0; i < children.length && flagDelOk; i++) {
                    flagDelOk = new File(Dir, children[i]).delete();
                }
            }

            if (flagDelOk) { flagDelOk = Dir.delete(); }

        } catch (Exception exc) {

            Log.d(TAG_Utils, "--> Erro: " + exc.getMessage());
            flagDelOk = false;
        }

        return flagDelOk;

    }

    //--------------------------------------------------------------------------
    //                     src/main/res/raw/ files m??todos
    //--------------------------------------------------------------------------
    //---------------------------------------------------------------------
    // Lista os arquivos existentes em: File1\app\src\main\res\raw
    //https://stackoverflow.com/questions/25178715/how-can-i-list-the-items-in-the-raw-folder-in-android/25179438
    //---------------------------------------------------------------------
    public String [] ListRaw() {

        String strMsg  = "- Arquivos Raw: ";
        String[] names = new String[1];

        try {

            Field fields[] = R.raw.class.getDeclaredFields();
            names = new String[fields.length];

            if (names.length > 0) {

                for (int i = 0; i < fields.length; i++) {
                    Field f  = fields[i];
                    names[i] = f.getName();
                    strMsg  += "\n" + names[i];
                }
            }
            else return (new String[]{"", ""});

        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.i(TAG_Utils, strMsg);

        return (names);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public ArrayList <String> LeituraFileRaw(Context context, String strFileName) {

        //--- Instancializa????es e inicializa????es
        InputStreamReader isr;
        BufferedReader    br;

        ArrayList<String> strArLstArq = new ArrayList<>();
        String line = new String();

        try {

            java.io.InputStream ins = context.getResources().openRawResource(
                    context.getResources().getIdentifier(strFileName, "raw",
                            context.getPackageName()));

            //--- Converte o inputStream em strArLst
            isr  = new InputStreamReader(ins, Charset.forName("UTF-8"));
            br   = new BufferedReader(isr);
            line = null;
            while ((line = br.readLine()) != null) {
                strArLstArq.add(line + "\n\r");
            }

            Log.d("TAG_Utils", "Leitura do arq raw:");
            for (String strArqLine : strArLstArq) {
                Log.d("TAG_Utils", (strArqLine + "\n\r"));
            }

        } catch (Exception exc) {
            Log.d("TAG_Utils", "--> Erro: " + exc.getMessage());
            strArLstArq = new ArrayList<>();
        }

        return (strArLstArq);
    }

    //--------------------------------------------------------------------------
    //                         M??todos auxiliares
    //--------------------------------------------------------------------------
    //--------------------------------------------------------------------------
    //--- Obt??m Uris granted para compartilhamento de arquivos externamente
    //--------------------------------------------------------------------------
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public  ArrayList<Uri> ObtemURIgranteds(Context contxt, String [] strArNomeArqs) {

        //--- Instancializa????es e inicializa????es
        boolean flagGranstOk = true;

        ArrayList<Uri> uriArLstPath = new ArrayList<Uri>();
        Uri uriPath = null;

        Log.d(TAG_Utils, "--> Obt??m URIs granteds");

        try {

            if (strArNomeArqs != null) {

                for (String strNomeArq : strArNomeArqs) {

                    File filelocation = new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS), strNomeArq);
                    Log.d(TAG_Utils, "- filelocation: " + filelocation.toString());
                    try {

                        uriPath = FileProvider.getUriForFile(contxt,
                                //"br.com.jhconsultores.apache.fileprovider",
                                "br.com.jhconsultores.apache.fileprovider",
                                filelocation);
                        uriArLstPath.add(uriPath);

                        Log.d(TAG_Utils, "granted!");

                    } catch (IllegalArgumentException e) {

                        Log.e(TAG_Utils, "Error: " + e.getMessage());
                        flagGranstOk = false;
                    }
                }
            }

        } catch (Exception exc) {
            Log.d(TAG_Utils, "Erro email: " + exc.getMessage());
            flagGranstOk = false;
        }

        String strMsgLog = new String();
        if (!flagGranstOk) {

            uriArLstPath = new ArrayList<>();
            strMsgLog    = "Not granteds";

        } else strMsgLog    = "Granteds!";

        Log.d(TAG_Utils, strMsgLog);

        return uriArLstPath;

    }

    //---------------------------------------------------------------------
    // Determina o ??ndice de um String num StringArray
    //---------------------------------------------------------------------
    public  int indexOf (String strNomeArq, String [] strArTodosArqRaw) {

        int intID   = -1;

        int intIndx = 0;
        for ( ; intIndx < strArTodosArqRaw.length; intIndx ++) {

            if (strNomeArq.equals(strArTodosArqRaw[intIndx])) {
                break;
            }
        }
        if (intIndx < strArTodosArqRaw.length) intID = intIndx;

        return intID;
    }

    //-------------------------------------------------------------------------
    // M??todo para apresentar AlarmDialog
    //-------------------------------------------------------------------------
    public  void UI_Dialog (Context context, String strTitle, String strDialogo,
                                           String BtnPos, final String BtnNeg, String BtnNeu) {

        //--- Instancializa????es e inicializa????es
        final int[] intRespUI = {-1};

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setIcon  (R.mipmap.ic_launcher);
        builder.setTitle (strTitle);
        //--- Mensagem ao usu??rio
        builder.setMessage(strDialogo);

        //--- Bot??o positivo
        if (BtnPos != null) {
            builder.setPositiveButton(BtnPos, new DialogInterface.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    /*
                    switch (mainActivity.intDialogResult) {

                        case 0: // N??o considera o tapping
                            break;
                        case 1: // Trata_btnColeta()
                            mainActivity.btnColeta.setEnabled  (true);
                            mainActivity.btnConsulta.setEnabled(false);
                            //mainActivity.Trata_btnColetaClick();
                            break;
                        case 2: // Vai ?? Consulta
                            mainActivity.Consulta();
                            break;

                        default:
                            break;
                    }
                */
                }
            });
        }
        //--- Bot??o Negativo
        if (BtnNeg != null) {
            builder.setNegativeButton(BtnNeg, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    /*
                    switch (mainActivity.intDialogResult) {

                        case 0: // N??o considera o tapping
                            break;
                        case 1: // Trata_btnColeta()
                            mainActivity.btnColeta.setEnabled  (true);
                            mainActivity.btnConsulta.setEnabled(false);
                            break;
                        case 2: // Retorna ao Contexto de quando clicou em btnColeta.
                            mainActivity.RecuperaColeta();
                            break;
                        default:
                            break;
                    }
                    */
                }
            });
        }
        //--- Bot??o Neutro
        if (BtnNeu != null) {
            builder.setNeutralButton(BtnNeu, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    /*
                    switch (mainActivity.intDialogResult) {

                        case 0: // N??o considera o tapping
                            break;

                        default:
                            break;
                    }
                     */
                }
            });
        }

        //--- Cria e apresenta o AlarmDialog
        AlertDialog dialog = builder.create();
        dialog.show();

        Button pbuttonPos = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        pbuttonPos.setTextColor(Color.BLUE);
        Button pbuttonNeg = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        pbuttonNeg.setTextColor(Color.RED);
        Button pbuttonNeu = dialog.getButton(DialogInterface.BUTTON_NEUTRAL);
        pbuttonNeu.setTextColor(Color.BLACK);
    }

    //-------------------------------------------------------------------------
    //                            Novo Progress Dialog
    // https://stackoverflow.com/questions/45373007/progressdialog-is-deprecated-what-is-the-alternate-one-to-use
    //-------------------------------------------------------------------------
    //-------------------------------------------------------------------------
    // Instancializa novo Progress Dialog
    // https://stackoverflow.com/questions/45373007/progressdialog-is-deprecated-what-is-the-alternate-one-to-use
     AlertDialog alertProgDialog = null;
     String strMsgPD             = new String();
    //-------------------------------------------------------------------------
    public  void setAlertProgressDialog(Context context, String strMsg) {

        //--- Se estiver apresentando msg, ANTES de apresentar uma nova, cancela a atual
        //--------------------------
        alertProgDialogDismiss();
        //--------------------------

        //--- Linear Layout
        LinearLayout ll = new LinearLayout(context);
        ll.setOrientation(LinearLayout.HORIZONTAL);
        int llPadding = 30;
        ll.setPadding(llPadding, llPadding, llPadding, llPadding);
        ll.setGravity(Gravity.CENTER);

        //--- ProgressBar
        LinearLayout.LayoutParams llParam = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        llParam.gravity = Gravity.CENTER;
        ll.setLayoutParams(llParam);

        ProgressBar progressBar = new ProgressBar(context);
        progressBar.setIndeterminate(true);
        progressBar.setPadding(0, 0, llPadding, 0);
        progressBar.setLayoutParams(llParam);
        ll.addView(progressBar);

        //--- TextView
        llParam = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        llParam.gravity = Gravity.CENTER;
        TextView tvText = new TextView(context);
        tvText.setText(strMsg);
        tvText.setTextColor(Color.parseColor("#000000"));
        tvText.setTextSize(20);
        tvText.setLayoutParams(llParam);
        ll.addView(tvText);

        //--- Alert Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder = new AlertDialog.Builder(context);
        builder.setCancelable(true);
        builder.setView(ll);

        alertProgDialog = builder.create();
        alertProgDialog.show();

        //--- Janela para apresenta????o
        Window window = alertProgDialog.getWindow();

        if (window != null) {

            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(alertProgDialog.getWindow().getAttributes());
            layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT;
            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            alertProgDialog.getWindow().setAttributes(layoutParams);

        }
    }

    //-------------------------------------------------------------------------
    // DesInstancializa novo Progress Dialog
    // https://stackoverflow.com/questions/45373007/progressdialog-is-deprecated-what-is-the-alternate-one-to-use
    //-------------------------------------------------------------------------
    public  boolean alertProgDialogDismiss() {

        boolean flagDismissOk = true;

        try {

            if (alertProgDialog.isShowing()) {

                Log.i(TAG_Utils, "aPD is showing; cancel it");
                alertProgDialog.cancel();
                strMsgPD = "";

            } else {
                Log.i(TAG_Utils, "aPD is not showing");
            }

        } catch (Exception exc) {

            flagDismissOk = false;

        }

        return flagDismissOk;
    }

    //--------------------------------------------------------------------------
    // Custom Toast
    //--------------------------------------------------------------------------
    public  void customToast(Context context, String msg) {

        Toast toastL          = Toast.makeText(context.getApplicationContext(), msg, Toast.LENGTH_SHORT);
        TextView toastMessage = toastL.getView().findViewById(android.R.id.message);

        toastMessage.setTextColor(Color.WHITE);
        toastMessage.setBackgroundColor(Color.parseColor("#8B4513"));  // saddlebrown
        // int textSizeInSp = (int) context.getResources().getDimension(R.dimen.result_font);
        toastMessage.setTextSize(convertSpToPixels(4 , context.getApplicationContext()));
        toastL.show();

    }

    //--------------------------------------------------------------------------
    // Retira os acentos do string recebido.
    //--------------------------------------------------------------------------
    public  String TirarAcentos (String strPalavraComAcento) {

        String strPalavraSemAcento = new String();

        String comAcentos = "????????????????????????????????????????????????????????????????????????????????????????????'";
        char [] chArComAc = comAcentos.toCharArray();

        String semAcentos = "AAAAAAaaaaaEEEEeeeeIIIIiiiiOOOOOoooooUUUuuuuCc_";
        char [] chArSemAc = semAcentos.toCharArray();

        for (int i = 0; i < chArSemAc.length; i++)   //.length(); i++)
        {
            strPalavraSemAcento = strPalavraComAcento.replace(chArComAc[i], chArSemAc[i]);
            strPalavraComAcento = strPalavraSemAcento;
        }
        strPalavraSemAcento = strPalavraComAcento;

        return strPalavraSemAcento;
    }

    //--------------------------------------------------------------------------
    //                               Tools
    //--------------------------------------------------------------------------
    //-------------------------------------------------------------------------
    // L?? data e hora do sistema e retorna os dados formatados conf. espec.
    // SDK <  Oreo (8.0.0) simpleDataFormat
    // SDK >= Oreo (8.0.0) "dd/MM/yyyy HH:mm:ss" ou "yyMMdd HHmmss"
    //-------------------------------------------------------------------------
    @RequiresApi(api = Build.VERSION_CODES.O)
    public String LeDataHora (String strFormatDataHora) {

        String agoraFormatado = null;

        // https://dicasdejava.com.br/java-8-como-formatar-localdate-e-localdatetime/
        //--- Fun????es v??lidas para API O (Oreo - 8.0.0) ou superior
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalDateTime agora         = null;
            DateTimeFormatter formatter = null;

            agora         = LocalDateTime.now();
            formatter     = DateTimeFormatter.ofPattern(strFormatDataHora);
            agoraFormatado= agora.format(formatter);
        }
        else {

            //agoraFormatado = (strFormatDataHora.equals("dd/MM/yyyy HH:mm:ss")) ? "01/01/2020 12:00:00" :
            //        ((strFormatDataHora.equals("yyMMdd HHmmss") ? "200101 120000" : "800101 120000"));

            Date date           = Calendar.getInstance().getTime();
            SimpleDateFormat df = new SimpleDateFormat(strFormatDataHora);
            agoraFormatado      = df.format(date);
        }
        return (agoraFormatado);
    }

    //--------------------------------------------------------------------------
    // Esconde o teclado
    //--------------------------------------------------------------------------
    public void EscondeTeclado(Activity activity) {

        View view = activity.getCurrentFocus();
        if (view != null) {

            // This will force the keyboard to be hidden in all situations.
            InputMethodManager imm = (InputMethodManager) activity.
                                                    getSystemService(activity.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

            // In some cases you will want to pass in InputMethodManager. HIDE_IMPLICIT_ONLY as
            // the second parameter to ensure you only hide the keyboard when the user didn't
            // explicitly force it to appear (by holding down menu).
            //InputMethodManager imm = (InputMethodManager) activity.getSystemService(activity.INPUT_METHOD_SERVICE);
            //imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        }
    }

    //--------------------------------------------------------------------------
    // Drawing
    //--------------------------------------------------------------------------
    public  float convertSpToPixels(float sp, Context context) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.getResources().getDisplayMetrics());
    }

    //--- Converte um valor em dp para pixels (px)
    // [002] - pag240
    public float toPixels (Context context, float dip) {

        Resources r     = context.getResources();
        float densidade = r.getDisplayMetrics().density;

        int px = (int) (dip * densidade + 0.5f);

        return dip;

    }

    //--------------------------------------------------------------------------
    //                            Apache
    //--------------------------------------------------------------------------

    //-------------------------------------------------------------------------
    // Verifica se os arquivos necess??rios para o App existem no raw.
    //-------------------------------------------------------------------------
    boolean VerificaExistArqsNecRaw(String [] strArNomeArqNeces) {

        //--- Instancializa????es e inicializa????es
        boolean flagExistem = false;
        String strMsgUI = "";
        String strMsg   = "";

        //--- Lista os arquivos existentes em raw
        //----------------------------------------
        String [] strArNomeArqExis = ListRaw();
        //----------------------------------------

        //--- Determina se falta algum dos arquivos
        try {

            ArrayList<String> strArLstArqNaoExistente = new ArrayList<>();

            int intIndx = 0;
            for (; intIndx < strArNomeArqNeces.length; intIndx++) {

                if (indexOf(strArNomeArqNeces[intIndx], strArNomeArqExis) == -1) {
                    strArLstArqNaoExistente.add(strArNomeArqNeces[intIndx]);
                }
            }

            if (strArLstArqNaoExistente.size() > 0) {

                for (String strArqNaoExist : strArLstArqNaoExistente) {
                    strMsg += "\n-" + strArqNaoExist;
                }
                strMsgUI = "\nOs arquivos abaixo N??O existem no res.raw:" + strMsg;

            } else {
                flagExistem = true;
                strMsgUI    = "\nTodos os arquivos necess??rios existem no res.raw";
            }
        } catch (Exception exc) {

            Log.d(TAG_Utils, "Erro: " + exc.getMessage());

        }
        Log.d(TAG_Utils, strMsgUI);

        return flagExistem;
    }

    //-------------------------------------------------------------------------
    // M??todo para leitura de um arquivo em src/main/res/raw/.
    //-------------------------------------------------------------------------

    //--------------------------------------------------------------------------
    // Verifica se existem os diret??rios e arquivos necess??rios para o App.
    // Caso negativo, tenta providenci??-los.
    //--------------------------------------------------------------------------
    public int apache_VerifInfra(Context context) {

        //--- Instancializa????es e inicializa????es
        String TAG_Utils   = "MAIN_LOG";
        int intInfraOk    = 0;
        String strNomeArq = new String();

        boolean flagDirExiste  = true;
        boolean flagCriado     = false;
        boolean flagFileExiste = true;
        File folder;
        File file;

        File envPath = Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS);
        Log.d(TAG_Utils, "- path: " + envPath.getName());
        //----------------------------------------------------------------------
        // A1- Diret??rio "Relatorios"
        //----------------------------------------------------------------------
        folder = new File(envPath + File.separator + "Relatorios");
        if (!folder.exists()) {
            Log.d(TAG_Utils, "- O diret??rio 'Relatorios' N??O existe. Cria-o");
            //---------------------------------
            flagDirExiste = folder.mkdirs();
            //---------------------------------
            if (flagDirExiste) {
                Log.d(TAG_Utils, "- OK!");
                flagCriado = true;
            } else {
                Log.d(TAG_Utils, "N??o OK!");
                intInfraOk = -1;
            }

        } else {
            Log.d(TAG_Utils, "- O diret??rio 'Relatorios' existe.");
        }

        //----------------------------------------------------------------------
        // A2- Diret??rio "Apache"
        //----------------------------------------------------------------------
        if (intInfraOk != -1) {

            folder = new File(envPath + File.separator + "Apache");
            if (!folder.exists()) {

                Log.d(TAG_Utils, "- O diret??rio 'Apache' N??O existe. Cria-o");
                //---------------------------------
                flagDirExiste = folder.mkdirs();
                //---------------------------------
                if (flagDirExiste) {
                    flagCriado = true;
                    Log.d(TAG_Utils, "- OK! Pend??ncias.");
                    intInfraOk = 10;    // Pend??ncia: preparar o arquivo com as cidades e estados
                } else {
                    flagCriado = false;
                    Log.d(TAG_Utils, "N??o OK!");
                    intInfraOk = -1;
                }
            }
            //--- O diret??rio Apache j?? existe, verifica se o arquivo de dados j?? existe tamb??m.
            else {
                Log.d(TAG_Utils, "- O diret??rio 'Apache' existe.");
            }
        }

        //----------------------------------------------------------------------
        // A3- Arquivos do diret??rio "Apache"
        //----------------------------------------------------------------------
        if (intInfraOk != -1 && !flagCriado) {

            strNomeArq = "EstadosEcidadesDoBrasil.txt";
            file = new File(envPath + File.separator + "Apache" +
                    File.separator + strNomeArq);
            if (!file.exists()) {
                Log.d(TAG_Utils, "- OK!");
                intInfraOk = 10;    // Pend??ncia: preparar o arquivo com as cidades e estados
            } else {
                Log.d(TAG_Utils, "- O arquivo existe.");
            }
        }

        //----------------------------------------------------------------------
        // A4- Diretorio "Teste_OffLine"
        //----------------------------------------------------------------------
        if (intInfraOk != -1) {

            folder = new File(envPath + File.separator + "Teste_OffLine");
            if (!folder.exists()) {

                Log.d(TAG_Utils, "- O diret??rio 'Teste_OffLine' N??O existe. Cria-o");
                //---------------------------------
                flagDirExiste = folder.mkdirs();
                //---------------------------------
                if (flagDirExiste) {
                    flagCriado = true;
                    Log.d(TAG_Utils, "- OK! Pend??ncias.");
                    intInfraOk++;    // Pend??ncia: preparar os arquivos para INIT e Href

                } else {
                    flagCriado = false;
                    Log.d(TAG_Utils, "N??o OK!");
                    intInfraOk = -1;
                }
            }
            else {
                Log.d(TAG_Utils, "- O diret??rio 'Teste_OffLine' existe.");
            }

            //----------------------------------------------------------------------
            // A5- Arquivos do diret??rio "Teste_OffLine"
            //----------------------------------------------------------------------
            if (intInfraOk != -1 && !flagCriado) {

                //--- O diret??rio Teste_OffLine j?? existe, verifica se os arquivos INIT e Href j?? existem tamb??m.
                //- Verifica arquivo Init 1
                String strNomeArqInit = "ApacheInit_Sorocaba_SP_p0001.txt";
                file = new File(envPath + File.separator + "Teste_OffLine" +
                        File.separator + strNomeArqInit);
                if (!file.exists()) {
                    Log.d(TAG_Utils, "- OK! Pend??ncias.");
                    if ((intInfraOk % 10) == 0)
                        intInfraOk++;    // Pend??ncia: preparar o arquivo com as cidades e estados
                } else {
                    Log.d(TAG_Utils, "- O arquivo Init 1 existe.");
                }
                //- Verifica arquivo Init 2
                if (file.exists()) {
                    strNomeArqInit = "ApacheInit_Sorocaba_SP_p0002.txt";
                    file = new File(envPath + File.separator + "Teste_OffLine" +
                            File.separator + strNomeArqInit);
                    if (!file.exists()) {
                        Log.d(TAG_Utils, "- OK! Pend??ncias.");
                        if ((intInfraOk % 10) == 0)
                            intInfraOk++;    // Pend??ncia: preparar o arquivo com as cidades e estados
                    } else {
                        Log.d(TAG_Utils, "- O arquivo Init 2 existe.");
                    }
                }
                //- Verifica arquivo Href pag0001
                if (file.exists()) {
                    strNomeArqInit = "ApacheHref_Sorocaba_SP_p0001.txt";
                    file = new File(envPath + File.separator + "Teste_OffLine" +
                            File.separator + strNomeArqInit);
                    if (!file.exists()) {
                        Log.d(TAG_Utils, "- OK! Pend??ncias.");
                        if ((intInfraOk % 10) == 0)
                            intInfraOk++;    // Pend??ncia: preparar o arquivo com as cidades e estados
                    } else {
                        Log.d(TAG_Utils, "- O arquivo Href pag0001 existe.");
                    }
                }
                //- Verifica arquivos Href
                if (file.exists()) {
                    int intIdx = 1;
                    for (; intIdx <= 20; intIdx++) {
                        String strNomeArqHref = String.format("ApacheHref_Sorocaba_SP_r%02d.txt", intIdx);
                        file = new File(envPath + File.separator  + "Teste_OffLine" +
                                File.separator + strNomeArqHref);
                        if (!file.exists()) {
                            Log.d(TAG_Utils, "- OK! Pend??ncias.");
                            if ((intInfraOk % 10) == 0)
                                intInfraOk++;    // Pend??ncia: preparar o arquivo com as cidades e estados
                            break;
                        }
                    }
                    if (intIdx > 20) {
                        Log.d(TAG_Utils, "- Os arquivos Href existem.");
                    }
                }
            }
        }

        //--- Prepara o retorno e retorna
        String strResp = (intInfraOk == -1) ? "- N??o ok!" : ((intInfraOk == 0) ? "Ok!" : "Com pend??ncias!");
        Log.d(TAG_Utils, "- Ret = " + String.valueOf(intInfraOk) + "   " + strResp);

        return (intInfraOk);
    }

}

//------------------------------------------------------------------------------
//  Classe auxiliar para a ordena????o da list file
//------------------------------------------------------------------------------
class Pair implements Comparable {

    //--- Instancializa vari??veis locais
    public long t;
    public File f;

    //--- Construtor
    public Pair(File file) {
        f = file;
        t = file.lastModified();
    }

    //--- M??todo
    public int compareTo(Object o) {
        long u = ((Pair) o).t;
        return t < u ? -1 : t == u ? 0 : 1;
    }

};
