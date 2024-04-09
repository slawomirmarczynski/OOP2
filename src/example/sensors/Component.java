/*
 * Copyright (c) 2024 Sławomir Marczyński. All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met: 1. Redistributions of source code must retain the above
 * copyright notice, this list of conditions and the following
 * disclaimer. 2. Redistributions in binary form must reproduce the
 * above copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided with
 * the distribution. 3. Neither the name of the copyright holder nor
 * the names of its contributors may be used to endorse or promote
 * products derived from this software without specific prior written
 * permission. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND
 * CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package example.sensors;

/**
 * Klasa Component jest klasą abstrakcyjną, która reprezentuje nazwany komponent
 * systemu składającego się z urządzeń, sensorów i odbiorców danych.
 */
public abstract class Component {

    // Nazwa komponentu jest finalna, czyli mogłaby być publiczna, a i tak
    // byłaby chroniona przed przypadkowymi zmianami. Jest jednak prywatna,
    // bo ma to zachęcić do korzystania z akcesora getName().
    //
    private final String name;

    /**
     * Konstruktor klasy Component nie tylko że jest konstruktorem którego
     * potrzebujemy (bo chcemy konstruować nazwane obiekty), ale także blokuje
     * utworzenie domyślnego konstruktora bezparametrowego (którego nie chcemy,
     * bo nie chcemy stwarzać komponentów bez nazwy).
     *
     * @param name nazwa komponentu.
     */
    public Component(String name) {
        this.name = name;
    }

    // Metoda zwracająca nazwę komponentu.

    /**
     * Akcesor zwracający nazwę komponentu.
     *
     * @return nazwa komponentu jako łańcuch znaków.
     */
    public String getName() {
        return name;
    }
}
