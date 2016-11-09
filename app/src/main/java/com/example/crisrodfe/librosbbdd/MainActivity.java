package com.example.crisrodfe.librosbbdd;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
{
    Spinner miSpinner;
    GridLayout miLayout;
    MiBBDD bd;
    ListView miListView;
    String orden = "titulo";
    AlertDialog levelDialog;
    AdaptadorPersonalizado ap;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bd  = new MiBBDD(getApplicationContext(),null,null,1);
        miLayout = (GridLayout) findViewById(R.id.miLayout);

        //Creación del spinner que mostrará las opciones de la aplicación
        String[] opciones = {"Elige una opción","Mostrar listado","Añadir elemento","Borrar elemento","Añadir datos iniciales"};
        miSpinner = (Spinner)findViewById(R.id.spinnerOpciones);
        ArrayAdapter<String> adaptador = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,opciones);
        miSpinner.setAdapter(adaptador);
        miSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                String opcionElegida = miSpinner.getSelectedItem().toString();

                switch (opcionElegida)
                {
                    case "Mostrar listado":
                        dialogoListado();
                        break;

                    case "Añadir datos iniciales": //Si algún registro en la base de datos lanza un mensaje. Si no hay, introduce los datos.
                        if(bd.getTitulos(orden).length >= 1)
                        {
                            Toast.makeText(MainActivity.this,"Ya existen registros en la BD.", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            bd.addLibro("El cuento de la criada","Margaret Atwood","S.A. EDICIONES B");
                            bd.addLibro("Los besos en el pan","Almudena Grandes","Tusqueta");
                            bd.addLibro("Pureza","Jonathan Frazen","Salamandra");
                            bd.addLibro("Entre los vivos","Gines Sanchez","Tusquets");
                            bd.addLibro("Pinball 1978","Haruki Murakami","Tusquets");
                            bd.addLibro("1Q84","Haruki Murakami","Tusquets");
                            bd.addLibro("Libertad","Jonathan Frazen","Salamandra");
                            bd.addLibro("Beatriz y los cuerpos celestes","Lucía Extebarría","Booket");
                            bd.addLibro("La fiesta del Chivo","Mario Vargas Llosa","Punto de Lectura");
                            bd.addLibro("Inés y la alegría","Almudena Grandes","Tusquets");
                            bd.addLibro("El género en disputa","Judith Butler","Paidos");
                            bd.addLibro("El segundo sexo","Simone de Beauvoir","Cátedra");

                        }
                        break;

                    case "Añadir elemento":
                        addData();
                        break;

                    case "Borrar elemento":
                        deleteData();
                        break;
                }

                miSpinner.setSelection(0);//Después de realizar la selección en el spinner volvemos a colocar en primer lugar
                                          // la opción 'Elige una opción'
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });
    }

    /**
     * Dialogo que aparece al pulsar 'Mostrar Listado'.
     * Aparecen como radioButtons las diferentes opciones.
     * MOdificará según la elección la variable 'orden' que utilizaremos como argumento a la hora de pedir los registros a la base de datos.
     * @return
     */
    public String dialogoListado()
    {
        final CharSequence[] items = {" Titulo "," Autor "," Editorial "};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Mostrar listado ordenado por:");
        builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int item) {

                switch(item)
                {
                    case 0:
                        orden = "titulo";
                        break;
                    case 1:
                        orden="autor";

                        break;
                    case 2:
                        orden="editorial";
                        break;
                }
                levelDialog.dismiss();
                mostrarListado();
                dialogoInfo();
            }
        });
        levelDialog = builder.create();
        levelDialog.show();

        return orden;
    }


    /**
     * Diálogo informativo que aparece después de que se muestre un listado.
     * Da información sobre la funcionalidad de la ListView.
     */
    public void dialogoInfo()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("Mantén pulsado sobre un registro\nsi quiere modificarlo.");

        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                levelDialog.dismiss();
            }
        });
        levelDialog = builder.create();
        levelDialog.show();

    }

    /**
     * Muestra una ListView con los registro de la base de datos.
     * Aparecerán ordenados según la variable 'orden' (manipulada a través del diálogo que aparece al pulsar 'Mostrar Listado ')
     */
    public void mostrarListado()
    {
        ap = new AdaptadorPersonalizado(getApplicationContext(),R.layout.fila_libro,bd.getTitulos(orden));
        miListView = new ListView(getApplication());
        miListView.setAdapter(ap);
        miLayout.removeAllViews();
        miLayout.addView(miListView);
        miListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
        {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
            {
                //Si se mantiene pulsado un item, aparece una nueva pantalla donde podremos modificar los datos de su registro.
                modificarData(parent.getItemAtPosition(position).toString());
                return false;
            }
        });
    }

    /**
     * Muestra una pantalla con los datos del registro pulsado en la ListView.
     * Al pulsar el botón se modificará el registro con los nuevo datos(de haberlos cambiado).
     * Se pueden moficar los campos que se quieran.
     * Tras puslar modificar se muestra un mensaje por pantalla y se vuelve a mostrar la pantalla del listado.
     *
     * @param libroElegido
     */
    public void modificarData(String libroElegido)
    {
        miLayout.removeAllViews();
        LayoutInflater inflater = getLayoutInflater();
        View miView = inflater.inflate(R.layout.add, miLayout, false);
        miLayout.addView(miView);
        TextView textViewModificar = (TextView)findViewById(R.id.textViewNuevoRegistro);
        textViewModificar.setText("MODIFICAR REGISTRO");

        Button buttonModificar = (Button)findViewById(R.id.buttonAdd);
        buttonModificar.setText("Modificar");
        String[] libro = bd.getLibro(libroElegido);
        final String tituloAntes = libro[0];
        final EditText titulo = (EditText)findViewById(R.id.editTextTitulo);
        titulo.setText(libro[0]);
        final EditText autor = (EditText)findViewById(R.id.editTextAutor);
        autor.setText(libro[1]);
        final EditText editorial = (EditText)findViewById(R.id.editTextEditorial);
        editorial.setText(libro[2]);

        buttonModificar.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                bd.modificarLibros(tituloAntes,titulo.getText().toString(),autor.getText().toString(),editorial.getText().toString());
                Toast.makeText(MainActivity.this,"Registro modificado.", Toast.LENGTH_SHORT).show();
                miLayout.removeAllViews();
                miLayout.addView(miListView);

            }
        });
    }


    /**
     * Muestra una pantalla cajas de texto para introducir los campos de un nuevo registro.
     * Hay que rellenar todos los campos para poder añadirlo a la base de datos.
     */
    public void addData()
    {
        miLayout.removeAllViews();
        LayoutInflater inflater = getLayoutInflater();
        View miView = inflater.inflate(R.layout.add, miLayout, false);
        miLayout.addView(miView);

        final EditText titulo = (EditText)findViewById(R.id.editTextTitulo);
        final EditText autor = (EditText)findViewById(R.id.editTextAutor);
        final EditText editorial = (EditText)findViewById(R.id.editTextEditorial);
        Button buttonAdd = (Button)findViewById(R.id.buttonAdd);

        buttonAdd.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String t = titulo.getText().toString();
                String a = autor.getText().toString();
                String e = editorial.getText().toString();
                if(a.equals("") || t.equals("") || e.equals(""))
                {
                    Toast.makeText(MainActivity.this,"Rellene todos los campos", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    bd.addLibro(t,a,e);
                    Toast.makeText(MainActivity.this,"Ha sido añadido un nuevo registro a la base de datos", Toast.LENGTH_SHORT).show();
                    mostrarListado();
                }
            }
        });
    }

    /**
     * Pide por pantalla el titulo del libro que se quiere eliminar.
     * Muestra mensaje al borrarlo, si no se introduce titulo o cuando el titulo no existe.
     */
    public void deleteData()
    {
        miLayout.removeAllViews();
        LayoutInflater inflater = getLayoutInflater();
        View miView = inflater.inflate(R.layout.delete, miLayout, false);
        miLayout.addView(miView);

        final EditText titulo = (EditText)findViewById(R.id.editTextDeleteTitulo);
        Button buttonDelete = (Button)findViewById(R.id.buttonDelete);
        buttonDelete.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String t = titulo.getText().toString();
                if(t.equals(""))
                {
                    Toast.makeText(MainActivity.this,"Debe introducir un título", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    if(bd.existeLibro(titulo.getText().toString()))
                    {
                        bd.deleteLibro(t);
                        Toast.makeText(MainActivity.this, "Se ha borrado correctamente el registro.", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(MainActivity.this, "No hay ningún registro con ese título.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    /**
     * Adaptador personalizado que se utilizará para construir la ListView.
     */
    public class AdaptadorPersonalizado extends ArrayAdapter<String>
    {
        ArrayList misLibros = bd.getLibros(orden);

        public AdaptadorPersonalizado(Context ctx, int txtViewResourceId, String[] objects)
        {
            super(ctx, txtViewResourceId, objects);

        }

        @Override
        public View getDropDownView(int position, View cnvtView, ViewGroup prnt)
        {
            return crearFilaPersonalizada(position, cnvtView, prnt);
        }


        @Override
        public View getView(int pos, View cnvtView, ViewGroup prnt)
        {
            return crearFilaPersonalizada(pos, cnvtView, prnt);
        }


        public View crearFilaPersonalizada(int position, View convertView, ViewGroup parent)
        {
            misLibros = bd.getLibros(orden);
            String[] libro = (String[]) misLibros.get(position);

            LayoutInflater inflater = getLayoutInflater();
            View miFila = inflater.inflate(R.layout.fila_libro, parent, false);

            TextView titulo = (TextView) miFila.findViewById(R.id.textViewTitulo);
            titulo.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            titulo.setText(libro[0]);

            TextView autor = (TextView) miFila.findViewById(R.id.textViewAutor);
            autor.setText(libro[1]);

            TextView editorial = (TextView) miFila.findViewById(R.id.textViewEditorial);
            editorial.setText("Ed:"+libro[2]);

            return miFila;
        }
    }

}


