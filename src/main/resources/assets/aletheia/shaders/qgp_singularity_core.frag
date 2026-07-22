uniform sampler2D texture;
uniform float time;

void main() {
    vec2 uv = gl_TexCoord[0].xy;
    vec4 color = texture2D(texture, uv);

    if (color.a > 0.01) {
        float r = 0.5 + 0.5 * sin(time * 4.0 + uv.x * 12.0);
        float g = 0.5 + 0.5 * sin(time * 4.0 + uv.y * 12.0 + 2.094);
        float b = 0.5 + 0.5 * sin(time * 4.0 + (uv.x + uv.y) * 12.0 + 4.188);

        vec3 aura = vec3(r, g, b);
        color.rgb = mix(color.rgb, aura, 0.4) + aura * 0.3;
    }

    gl_FragColor = color;
}
